package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.MessageMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.ChatStreamProcessor;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.MessageServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceSendMessageUnitTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private SessionService sessionService;
    @Mock
    private UserSubscriptionService userSubscriptionService;
    @Mock
    private ApiKeyService apiKeyService;
    @Mock
    private ContextBuilder contextBuilder;
    @Mock
    private IdempotencyCache idempotencyCache;
    @Mock
    private ChatStreamProcessor chatStreamProcessor;
    @Mock
    private ChatProperty chatProperty;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Nested
    class SendMessage {

        @Test
        void when_sendMessage_then_userMessagePersisted() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, "gpt-4o", null);

            stubPrerequisites(userId, sessionId, session);

            SseEmitter emitter = messageService.sendMessage(userId, sessionId,
                    SendMessageRequest.builder().content("привет").build());

            assertNotNull(emitter);
            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepository).save(captor.capture());
            assertEquals(MessageRole.USER, captor.getValue().getRole());
            assertEquals("привет", captor.getValue().getContent());
            verify(chatStreamProcessor).process(any(ChatStreamProcessor.StreamTask.class));
        }

        @Test
        void when_sendMessage_withForeignSession_then_businessException_s001() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            doThrow(new BusinessException("s001", "Чат не найден"))
                    .when(sessionService).findEntityById(userId, sessionId);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> messageService.sendMessage(userId, sessionId,
                            SendMessageRequest.builder().content("текст").build()));

            assertEquals("s001", ex.getLocation());
            verify(messageRepository, never()).save(any());
            verify(chatStreamProcessor, never()).process(any());
        }

        @Test
        void when_sendMessage_withExpiredSubscription_then_businessException_u001() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, "gpt-4o", null);

            when(sessionService.findEntityById(userId, sessionId)).thenReturn(session);
            when(userSubscriptionService.findActiveSubscription(userId))
                    .thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> messageService.sendMessage(userId, sessionId,
                            SendMessageRequest.builder().content("текст").build()));

            assertEquals("u001", ex.getLocation());
            verify(messageRepository, never()).save(any());
        }

        @Test
        void when_sendMessage_withSystemPrompt_then_systemPromptPassedToContext() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, "gpt-4o", "ты помощник");
            List<ChatMessage> context = List.of(
                    ChatMessage.builder().role("system").content("ты помощник").build(),
                    ChatMessage.builder().role("user").content("вопрос").build()
            );

            stubPrerequisites(userId, sessionId, session);
            when(contextBuilder.build(eq(session), any(), eq("вопрос"))).thenReturn(context);

            messageService.sendMessage(userId, sessionId,
                    SendMessageRequest.builder().content("вопрос").build());

            ArgumentCaptor<ChatStreamProcessor.StreamTask> taskCaptor =
                    ArgumentCaptor.forClass(ChatStreamProcessor.StreamTask.class);
            verify(chatStreamProcessor).process(taskCaptor.capture());
            assertEquals("system", taskCaptor.getValue().chatMessages().get(0).getRole());
        }
    }

    private void stubPrerequisites(UUID userId, UUID sessionId, Session session) {
        when(sessionService.findEntityById(userId, sessionId)).thenReturn(session);
        when(userSubscriptionService.findActiveSubscription(userId))
                .thenReturn(Optional.of(UserSubscription.builder().build()));
        when(apiKeyService.findActiveVirtualKey(userId)).thenReturn("sk-test-key");
        when(messageRepository.findTop50BySessionIdOrderByCreateTimeDesc(sessionId))
                .thenReturn(List.of());
        when(contextBuilder.build(any(), any(), any())).thenReturn(List.of());
        when(chatProperty.getSseTimeoutMs()).thenReturn(120000L);
    }

    private Session buildSession(UUID sessionId, String model, String systemPrompt) {
        return Session.builder()
                .id(sessionId)
                .userId(UUID.randomUUID())
                .model(model)
                .systemPrompt(systemPrompt)
                .build();
    }
}
