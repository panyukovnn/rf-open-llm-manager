package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UsageEventResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.ApiKey;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.model.UsageEvent;
import ru.panyukovnn.rfopenllmbillingmanager.repository.ApiKeyRepository;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.repository.UsageEventRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.UsageTrackingServiceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageTrackingServiceImplUnitTest {

    @Mock
    private UsageEventRepository usageEventRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private UserSubscriptionService userSubscriptionService;

    @InjectMocks
    private UsageTrackingServiceImpl usageTrackingService;

    @Nested
    class ProcessUsageCallback {

        @Test
        void when_processUsageCallback_then_usageEventSavedAndTokensDeducted() {
            UUID userId = UUID.randomUUID();
            UUID apiKeyId = UUID.randomUUID();
            String keyHash = "hash-value";
            ApiKey apiKey = ApiKey.builder()
                    .id(apiKeyId)
                    .appUserId(userId)
                    .keyHash(keyHash)
                    .build();
            LitellmUsageCallbackPayload payload = LitellmUsageCallbackPayload.builder()
                    .id("call-abc")
                    .userApiKeyHash(keyHash)
                    .model("gpt-4")
                    .promptTokens(100L)
                    .completionTokens(50L)
                    .totalTokens(150L)
                    .responseCost(new BigDecimal("0.0123"))
                    .build();

            when(apiKeyRepository.findByKeyHash(keyHash))
                    .thenReturn(Optional.of(apiKey));

            usageTrackingService.processUsageCallback(List.of(payload));

            ArgumentCaptor<UsageEvent> captor = ArgumentCaptor.forClass(UsageEvent.class);
            verify(usageEventRepository).save(captor.capture());
            UsageEvent saved = captor.getValue();
            assertEquals(apiKeyId, saved.getApiKeyId());
            assertEquals(userId, saved.getAppUserId());
            assertEquals("gpt-4", saved.getModel());
            assertEquals(150L, saved.getTotalTokens());
            assertEquals("call-abc", saved.getLitellmCallId());
            verify(userSubscriptionService).deductTokens(userId, 150L);
        }

        @Test
        void when_processUsageCallback_withBatch_then_allEventsProcessed() {
            UUID userId = UUID.randomUUID();
            ApiKey apiKey = ApiKey.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .keyHash("hash")
                    .build();
            LitellmUsageCallbackPayload payloadOne = LitellmUsageCallbackPayload.builder()
                    .id("call-one")
                    .userApiKeyHash("hash")
                    .totalTokens(100L)
                    .build();
            LitellmUsageCallbackPayload payloadTwo = LitellmUsageCallbackPayload.builder()
                    .id("call-two")
                    .userApiKeyHash("hash")
                    .totalTokens(200L)
                    .build();

            when(apiKeyRepository.findByKeyHash("hash"))
                    .thenReturn(Optional.of(apiKey));

            usageTrackingService.processUsageCallback(List.of(payloadOne, payloadTwo));

            verify(usageEventRepository, times(2)).save(any(UsageEvent.class));
            verify(userSubscriptionService).deductTokens(userId, 100L);
            verify(userSubscriptionService).deductTokens(userId, 200L);
        }

        @Test
        void when_processUsageCallback_withUnknownKeyHash_then_eventSkippedAndLogged() {
            LitellmUsageCallbackPayload payload = LitellmUsageCallbackPayload.builder()
                    .id("call-x")
                    .userApiKeyHash("unknown-hash")
                    .totalTokens(100L)
                    .build();

            when(apiKeyRepository.findByKeyHash("unknown-hash"))
                    .thenReturn(Optional.empty());

            usageTrackingService.processUsageCallback(List.of(payload));

            verify(usageEventRepository, never()).save(any(UsageEvent.class));
            verify(userSubscriptionService, never()).deductTokens(any(UUID.class), anyLong());
        }
    }

    @Nested
    class RecordChatUsage {

        @Test
        void when_recordChatUsage_then_usageEventPersistedWithCorrectTokens() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();
            Message assistantMessage = Message.builder()
                    .sessionId(sessionId)
                    .role(MessageRole.ASSISTANT)
                    .content("Ответ модели")
                    .tokensIn(100)
                    .tokensOut(50)
                    .model("gpt-4o")
                    .build();

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message message = invocation.getArgument(0);
                message.setId(messageId);

                return message;
            });

            usageTrackingService.recordChatUsage(userId, sessionId, assistantMessage);

            ArgumentCaptor<UsageEvent> captor = ArgumentCaptor.forClass(UsageEvent.class);
            verify(usageEventRepository).save(captor.capture());
            UsageEvent saved = captor.getValue();
            assertEquals(userId, saved.getAppUserId());
            assertEquals(sessionId, saved.getSessionId());
            assertEquals(messageId, saved.getMessageId());
            assertEquals("gpt-4o", saved.getModel());
            assertEquals(100L, saved.getPromptTokens());
            assertEquals(50L, saved.getCompletionTokens());
            assertEquals(150L, saved.getTotalTokens());
            verify(userSubscriptionService).deductTokens(userId, 150L);
        }

        @Test
        void when_recordChatUsage_withNullTokens_then_noTokensDeducted() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            UUID messageId = UUID.randomUUID();
            Message assistantMessage = Message.builder()
                    .sessionId(sessionId)
                    .role(MessageRole.ASSISTANT)
                    .content("Ответ модели")
                    .model("gpt-4o")
                    .build();

            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message message = invocation.getArgument(0);
                message.setId(messageId);

                return message;
            });

            usageTrackingService.recordChatUsage(userId, sessionId, assistantMessage);

            verify(usageEventRepository).save(any(UsageEvent.class));
            verify(userSubscriptionService, never()).deductTokens(any(UUID.class), anyLong());
        }
    }

    @Nested
    class FindUsageHistory {

        @Test
        void when_findUsageHistory_then_success() {
            UUID userId = UUID.randomUUID();
            Instant from = Instant.parse("2026-04-01T00:00:00Z");
            Instant to = Instant.parse("2026-04-30T23:59:59Z");
            UsageEvent eventOne = UsageEvent.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .model("gpt-4")
                    .promptTokens(10L)
                    .completionTokens(20L)
                    .totalTokens(30L)
                    .build();
            eventOne.setCreateTime(Instant.parse("2026-04-10T10:00:00Z"));
            UsageEvent eventTwo = UsageEvent.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .model("gpt-3.5")
                    .promptTokens(5L)
                    .completionTokens(15L)
                    .totalTokens(20L)
                    .build();
            eventTwo.setCreateTime(Instant.parse("2026-04-12T10:00:00Z"));

            when(usageEventRepository.findAllByAppUserIdAndCreateTimeBetween(eq(userId), eq(from), eq(to)))
                    .thenReturn(List.of(eventOne, eventTwo));

            List<UsageEventResponse> result = usageTrackingService.findUsageHistory(userId, from, to);

            assertEquals(2, result.size());
            assertEquals("gpt-4", result.get(0).getModel());
            assertEquals(30L, result.get(0).getTotalTokens());
            assertEquals("gpt-3.5", result.get(1).getModel());
            assertEquals(20L, result.get(1).getTotalTokens());
        }
    }
}
