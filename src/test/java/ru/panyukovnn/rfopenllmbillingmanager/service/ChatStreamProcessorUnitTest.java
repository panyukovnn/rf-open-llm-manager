package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.ChatStreamProcessor;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatStreamProcessorUnitTest {

    @Mock
    private LitellmClient litellmClient;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private SessionService sessionService;
    @Mock
    private IdempotencyCache idempotencyCache;
    @Mock
    private ChatProperty chatProperty;

    private ChatStreamProcessor processor;

    @BeforeEach
    void setUp() {
        Executor syncExecutor = Runnable::run;
        processor = new ChatStreamProcessor(
                litellmClient, messageRepository, sessionService,
                idempotencyCache, chatProperty, syncExecutor);
    }

    @Nested
    class Process {

        @Test
        void when_sendMessage_then_assistantMessagePersisted() {
            UUID sessionId = UUID.randomUUID();
            UUID savedId = UUID.randomUUID();
            ChatStreamProcessor.StreamTask task = buildTask(sessionId);
            Iterator<ChatCompletionChunk> chunks = buildChunks("Привет", "!").iterator();

            when(chatProperty.getReserveTokens()).thenReturn(1024);
            when(litellmClient.streamCompletion(eq("sk-key"), any())).thenReturn(chunks);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message message = invocation.getArgument(0);
                message.setId(savedId);

                return message;
            });

            processor.process(task);

            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepository).save(captor.capture());
            Message saved = captor.getValue();
            assertEquals(MessageRole.ASSISTANT, saved.getRole());
            assertEquals("Привет!", saved.getContent());
            assertEquals(sessionId, saved.getSessionId());
        }

        @Test
        void when_sendMessage_then_sessionLastUpdateTimeChanged() {
            UUID sessionId = UUID.randomUUID();
            UUID savedId = UUID.randomUUID();
            ChatStreamProcessor.StreamTask task = buildTask(sessionId);
            Iterator<ChatCompletionChunk> chunks = buildChunks("ok").iterator();

            when(chatProperty.getReserveTokens()).thenReturn(1024);
            when(litellmClient.streamCompletion(eq("sk-key"), any())).thenReturn(chunks);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message message = invocation.getArgument(0);
                message.setId(savedId);

                return message;
            });

            processor.process(task);

            verify(sessionService).touchLastUpdateTime(sessionId);
        }

        @Test
        void when_sendMessage_withLlmFailure_then_errorChunkSentAndNoAssistantMessageSaved() {
            UUID sessionId = UUID.randomUUID();
            ChatStreamProcessor.StreamTask task = buildTask(sessionId);

            when(chatProperty.getReserveTokens()).thenReturn(1024);
            when(litellmClient.streamCompletion(eq("sk-key"), any()))
                    .thenThrow(new BusinessException("ad65", "LLM-апстрим недоступен"));

            processor.process(task);

            verify(messageRepository, never()).save(any());
            verify(sessionService, never()).touchLastUpdateTime(any());
        }
    }

    private ChatStreamProcessor.StreamTask buildTask(UUID sessionId) {
        return ChatStreamProcessor.StreamTask.builder()
                .emitter(new SseEmitter(120000L))
                .userId(UUID.randomUUID())
                .sessionId(sessionId)
                .virtualKey("sk-key")
                .model("gpt-4o")
                .chatMessages(List.of(ChatMessage.builder().role("user").content("привет").build()))
                .build();
    }

    private List<ChatCompletionChunk> buildChunks(String... tokens) {
        return List.of(tokens).stream()
                .map(this::tokenChunk)
                .toList();
    }

    private ChatCompletionChunk tokenChunk(String content) {
        return ChatCompletionChunk.builder()
                .choices(List.of(ChatCompletionChunk.Choice.builder()
                        .delta(ChatCompletionChunk.Delta.builder()
                                .content(content)
                                .build())
                        .build()))
                .build();
    }
}
