package ru.panyukovnn.llmrfrouterbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.llmrfrouterbillingmanager.client.LitellmClient;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.exception.NoActiveSubscriptionException;
import ru.panyukovnn.llmrfrouterbillingmanager.model.ApiKey;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.ApiKeyRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.impl.ApiKeyServiceImpl;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceImplUnitTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;
    @Mock
    private LitellmClient litellmClient;
    @Mock
    private UserSubscriptionService userSubscriptionService;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    @Nested
    class GenerateKey {

        @Test
        void when_generateKey_then_keyCreatedInLitellmAndSaved() {
            UUID userId = UUID.randomUUID();
            String keyName = "test-key";
            UserSubscription subscription = UserSubscription.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .status(SubscriptionStatus.ACTIVE)
                    .build();
            LitellmKeyGenerateResponse litellmResponse = LitellmKeyGenerateResponse.builder()
                    .key("sk-generated-key-value")
                    .keyId("litellm-key-id")
                    .build();

            when(userSubscriptionService.findActiveSubscription(userId))
                    .thenReturn(Optional.of(subscription));
            when(litellmClient.generateKey(any()))
                    .thenReturn(litellmResponse);
            when(apiKeyRepository.save(any(ApiKey.class)))
                    .thenAnswer(invocation -> {
                        ApiKey saved = invocation.getArgument(0);
                        saved.setId(UUID.randomUUID());

                        return saved;
                    });

            CreateApiKeyResponse result = apiKeyService.generateKey(userId, keyName);

            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(keyName, result.getName());
            assertEquals("sk-generated-key-value", result.getKey());
            verify(litellmClient).generateKey(any());
            verify(apiKeyRepository).save(any(ApiKey.class));
        }

        @Test
        void when_generateKey_withNoActiveSubscription_then_noActiveSubscriptionException() {
            UUID userId = UUID.randomUUID();

            when(userSubscriptionService.findActiveSubscription(userId))
                    .thenReturn(Optional.empty());

            assertThrows(NoActiveSubscriptionException.class,
                    () -> apiKeyService.generateKey(userId, "test-key"));

            verify(litellmClient, never()).generateKey(any());
            verify(apiKeyRepository, never()).save(any());
        }
    }

    @Nested
    class RevokeKey {

        @Test
        void when_revokeKey_then_keyDeactivatedInLitellmAndDb() {
            UUID userId = UUID.randomUUID();
            UUID keyId = UUID.randomUUID();
            ApiKey apiKey = ApiKey.builder()
                    .id(keyId)
                    .appUserId(userId)
                    .litellmKeyId("litellm-key-id")
                    .active(true)
                    .build();

            when(apiKeyRepository.findById(keyId))
                    .thenReturn(Optional.of(apiKey));
            when(apiKeyRepository.save(any(ApiKey.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            apiKeyService.revokeKey(userId, keyId);

            assertFalse(apiKey.getActive());
            assertNotNull(apiKey.getRevokedAt());
            verify(litellmClient).revokeKey("litellm-key-id");
            verify(apiKeyRepository).save(apiKey);
        }

        @Test
        void when_revokeKey_withOtherUsersKey_then_accessDeniedException() {
            UUID userId = UUID.randomUUID();
            UUID otherUserId = UUID.randomUUID();
            UUID keyId = UUID.randomUUID();
            ApiKey apiKey = ApiKey.builder()
                    .id(keyId)
                    .appUserId(otherUserId)
                    .litellmKeyId("litellm-key-id")
                    .active(true)
                    .build();

            when(apiKeyRepository.findById(keyId))
                    .thenReturn(Optional.of(apiKey));

            assertThrows(BusinessException.class,
                    () -> apiKeyService.revokeKey(userId, keyId));

            verify(litellmClient, never()).revokeKey(any());
            verify(apiKeyRepository, never()).save(any());
        }
    }

    @Nested
    class FindUserKeys {

        @Test
        void when_findUserKeys_then_success() {
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.now();
            ApiKey apiKey1 = ApiKey.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .name("key-one")
                    .keyHash("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
                    .active(true)
                    .build();
            apiKey1.setCreateTime(createdAt);
            ApiKey apiKey2 = ApiKey.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .name("key-two")
                    .keyHash("1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef")
                    .active(true)
                    .build();
            apiKey2.setCreateTime(createdAt);

            when(apiKeyRepository.findAllByAppUserIdAndActiveTrue(userId))
                    .thenReturn(List.of(apiKey1, apiKey2));

            List<ApiKeyResponse> result = apiKeyService.findUserKeys(userId);

            assertEquals(2, result.size());
            assertEquals("key-one", result.get(0).getName());
            assertEquals("key-two", result.get(1).getName());
            assertNotNull(result.get(0).getKeyPrefix());
            assertNotNull(result.get(1).getKeyPrefix());
        }
    }

    @Nested
    class DeactivateAllUserKeys {

        @Test
        void when_deactivateAllUserKeys_then_allKeysRevoked() {
            UUID userId = UUID.randomUUID();
            ApiKey apiKey1 = ApiKey.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .litellmKeyId("litellm-key-id-one")
                    .active(true)
                    .build();
            ApiKey apiKey2 = ApiKey.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .litellmKeyId("litellm-key-id-two")
                    .active(true)
                    .build();

            when(apiKeyRepository.findAllByAppUserIdAndActiveTrue(userId))
                    .thenReturn(List.of(apiKey1, apiKey2));
            when(apiKeyRepository.save(any(ApiKey.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            apiKeyService.deactivateAllUserKeys(userId);

            assertFalse(apiKey1.getActive());
            assertFalse(apiKey2.getActive());
            assertNotNull(apiKey1.getRevokedAt());
            assertNotNull(apiKey2.getRevokedAt());
            verify(litellmClient).revokeKey("litellm-key-id-one");
            verify(litellmClient).revokeKey("litellm-key-id-two");
            verify(apiKeyRepository, times(2)).save(any(ApiKey.class));
        }
    }
}
