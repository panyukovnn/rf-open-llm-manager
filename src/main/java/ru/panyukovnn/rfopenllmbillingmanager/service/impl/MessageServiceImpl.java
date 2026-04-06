package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.MessageMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.ApiKeyService;
import ru.panyukovnn.rfopenllmbillingmanager.service.ContextBuilder;
import ru.panyukovnn.rfopenllmbillingmanager.service.IdempotencyCache;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final SessionService sessionService;
    private final UserSubscriptionService userSubscriptionService;
    private final ApiKeyService apiKeyService;
    private final ContextBuilder contextBuilder;
    private final IdempotencyCache idempotencyCache;
    private final ChatStreamProcessor chatStreamProcessor;
    private final ChatProperty chatProperty;

    @Override
    public List<MessageResponse> findBySession(UUID userId, UUID sessionId, int page, int size) {
        sessionService.findById(userId, sessionId);

        return messageRepository.findAllBySessionIdOrderByCreateTimeAsc(sessionId, PageRequest.of(page, size))
                .map(messageMapper::toMessageResponse)
                .getContent();
    }

    @Override
    public SseEmitter sendMessage(UUID userId, UUID sessionId, SendMessageRequest request) {
        Session session = sessionService.findEntityById(userId, sessionId);
        validateActiveSubscription(userId);

        Optional<SseEmitter> cached = tryResolveIdempotent(userId, request.getIdempotencyKey());

        if (cached.isPresent()) {
            return cached.get();
        }
        String virtualKey = apiKeyService.findActiveVirtualKey(userId);
        List<Message> history = loadHistory(sessionId);
        List<ChatMessage> context = contextBuilder.build(session, history, request.getContent());
        saveUserMessage(sessionId, request.getContent(), session.getModel());

        return startStreamTask(userId, session, virtualKey, context, request.getIdempotencyKey());
    }

    /**
     * Проверяет наличие активной подписки с бизнес-кодом u001
     */
    private void validateActiveSubscription(UUID userId) {
        userSubscriptionService.findActiveSubscription(userId)
                .orElseThrow(() -> new BusinessException("u001", "Подписка неактивна"));
    }

    /**
     * Возвращает готовый SseEmitter с DONE-чанком, если запрос уже был обработан ранее
     */
    private Optional<SseEmitter> tryResolveIdempotent(UUID userId, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        return idempotencyCache.find(userId, idempotencyKey)
                .map(messageId -> chatStreamProcessor.buildReplayEmitter(messageId, chatProperty.getSseTimeoutMs()));
    }

    /**
     * Загружает историю сессии, усечённую под maxHistoryMessages
     */
    private List<Message> loadHistory(UUID sessionId) {
        List<Message> recent = messageRepository.findTop50BySessionIdOrderByCreateTimeDesc(sessionId);

        return recent.stream()
                .sorted(Comparator.comparing(Message::getCreateTime))
                .toList();
    }

    private void saveUserMessage(UUID sessionId, String content, String model) {
        Message userMessage = Message.builder()
                .sessionId(sessionId)
                .role(MessageRole.USER)
                .content(content)
                .model(model)
                .build();
        messageRepository.save(userMessage);
    }

    private SseEmitter startStreamTask(UUID userId, Session session, String virtualKey,
                                       List<ChatMessage> context, String idempotencyKey) {
        SseEmitter emitter = new SseEmitter(chatProperty.getSseTimeoutMs());
        emitter.onTimeout(emitter::complete);
        emitter.onCompletion(() -> { /* no-op: ресурсы освобождаются при завершении итератора */ });

        ChatStreamProcessor.StreamTask task = ChatStreamProcessor.StreamTask.builder()
                .emitter(emitter)
                .userId(userId)
                .sessionId(session.getId())
                .virtualKey(virtualKey)
                .model(session.getModel())
                .chatMessages(context)
                .idempotencyKey(idempotencyKey)
                .build();
        chatStreamProcessor.process(task);

        return emitter;
    }
}
