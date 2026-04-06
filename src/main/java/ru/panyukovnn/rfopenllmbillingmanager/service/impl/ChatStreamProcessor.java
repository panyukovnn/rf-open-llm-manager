package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageChunkType;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.IdempotencyCache;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UsageTrackingService;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatStreamProcessor {

    private static final String SAFE_ERROR_MESSAGE = "Не удалось получить ответ модели";

    private final LitellmClient litellmClient;
    private final MessageRepository messageRepository;
    private final UsageTrackingService usageTrackingService;
    private final SessionService sessionService;
    private final IdempotencyCache idempotencyCache;
    private final ChatProperty chatProperty;
    @Qualifier("chatStreamingExecutor")
    private final Executor chatStreamingExecutor;

    public void process(StreamTask task) {
        chatStreamingExecutor.execute(() -> executeSafely(task));
    }

    /**
     * Создаёт эмиттер с готовым DONE-чанком — используется при идемпотентном повторе
     */
    public SseEmitter buildReplayEmitter(UUID assistantMessageId, long sseTimeoutMs) {
        SseEmitter emitter = new SseEmitter(sseTimeoutMs);

        try {
            sendDone(emitter, assistantMessageId);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Выполняет обработку стрима с общей обёрткой обработки исключений
     */
    private void executeSafely(StreamTask task) {
        try {
            runStream(task);
        } catch (Exception e) {
            handleFailure(task.emitter(), e);
        }
    }

    /**
     * Основная последовательность: обращение к LiteLLM, трансляция чанков, сохранение ответа
     */
    private void runStream(StreamTask task) throws IOException {
        ChatCompletionRequest upstreamRequest = buildUpstreamRequest(task);
        Iterator<ChatCompletionChunk> chunks = litellmClient.streamCompletion(task.virtualKey(), upstreamRequest);

        StringBuilder accumulated = new StringBuilder();
        TokenUsage usage = streamChunks(task.emitter(), chunks, accumulated);

        UUID assistantMessageId = persistAssistantMessage(task, accumulated.toString(), usage);
        storeIdempotency(task, assistantMessageId);
        sessionService.touchLastUpdateTime(task.sessionId());
        sendDone(task.emitter(), assistantMessageId);
    }

    /**
     * Собирает запрос в LiteLLM с учётом резерва токенов на ответ
     */
    private ChatCompletionRequest buildUpstreamRequest(StreamTask task) {
        return ChatCompletionRequest.builder()
                .model(task.model())
                .messages(task.chatMessages())
                .stream(Boolean.TRUE)
                .maxTokens(chatProperty.getReserveTokens())
                .build();
    }

    /**
     * Итерирует чанки upstream и отправляет TOKEN-чанки клиенту, накапливая контент
     */
    private TokenUsage streamChunks(SseEmitter emitter, Iterator<ChatCompletionChunk> chunks, StringBuilder accumulated) throws IOException {
        TokenUsage usage = new TokenUsage();

        while (chunks.hasNext()) {
            ChatCompletionChunk chunk = chunks.next();
            captureUsage(chunk, usage);
            String content = extractDeltaContent(chunk);

            if (content == null) {
                continue;
            }
            accumulated.append(content);
            emitter.send(tokenChunk(content));
        }

        return usage;
    }

    private String extractDeltaContent(ChatCompletionChunk chunk) {
        if (chunk.getChoices() == null || chunk.getChoices().isEmpty()) {
            return null;
        }
        ChatCompletionChunk.Choice choice = chunk.getChoices().get(0);

        if (choice.getDelta() == null) {
            return null;
        }

        return choice.getDelta().getContent();
    }

    private void captureUsage(ChatCompletionChunk chunk, TokenUsage usage) {
        if (chunk.getUsage() == null) {
            return;
        }

        if (chunk.getUsage().getPromptTokens() != null) {
            usage.tokensIn = chunk.getUsage().getPromptTokens();
        }

        if (chunk.getUsage().getCompletionTokens() != null) {
            usage.tokensOut = chunk.getUsage().getCompletionTokens();
        }
    }

    /**
     * Сохраняет assistant-сообщение и записывает usage в одной транзакции
     */
    private UUID persistAssistantMessage(StreamTask task, String content, TokenUsage usage) {
        Message assistantMessage = Message.builder()
                .sessionId(task.sessionId())
                .role(MessageRole.ASSISTANT)
                .content(content)
                .tokensIn(usage.tokensIn)
                .tokensOut(usage.tokensOut)
                .model(task.model())
                .build();
        Message saved = usageTrackingService.recordChatUsage(
                task.userId(), task.sessionId(), assistantMessage);

        return saved.getId();
    }

    private void storeIdempotency(StreamTask task, UUID assistantMessageId) {
        if (task.idempotencyKey() == null || task.idempotencyKey().isBlank()) {
            return;
        }
        idempotencyCache.store(task.userId(), task.idempotencyKey(), assistantMessageId);
    }

    private void sendDone(SseEmitter emitter, UUID assistantMessageId) throws IOException {
        MessageChunk chunk = MessageChunk.builder()
                .type(MessageChunkType.DONE)
                .messageId(assistantMessageId)
                .build();

        emitter.send(chunk);
        emitter.complete();
    }

    private MessageChunk tokenChunk(String content) {
        return MessageChunk.builder()
                .type(MessageChunkType.TOKEN)
                .content(content)
                .build();
    }

    /**
     * Обрабатывает исключение — отправляет ERROR-чанк клиенту, логирует стектрейс
     */
    private void handleFailure(SseEmitter emitter, Exception e) {
        log.error("Ошибка обработки стрима ответа модели: {}", e.getMessage(), e);
        String clientMessage = e instanceof BusinessException businessException
                ? businessException.getDisplayMessage()
                : SAFE_ERROR_MESSAGE;
        MessageChunk errorChunk = MessageChunk.builder()
                .type(MessageChunkType.ERROR)
                .content(clientMessage)
                .build();

        try {
            emitter.send(errorChunk);
            emitter.complete();
        } catch (Exception sendException) {
            log.warn("Не удалось отправить ERROR-чанк клиенту: {}", sendException.getMessage());
            emitter.completeWithError(sendException);
        }
    }

    @Builder
    public record StreamTask(
            SseEmitter emitter,
            UUID userId,
            UUID sessionId,
            String virtualKey,
            String model,
            List<ChatMessage> chatMessages,
            String idempotencyKey) {
    }

    private static final class TokenUsage {

        private Integer tokensIn;
        private Integer tokensOut;
    }
}
