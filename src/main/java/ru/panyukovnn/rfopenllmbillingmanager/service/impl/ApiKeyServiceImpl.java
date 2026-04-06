package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateApiKeyResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.rfopenllmbillingmanager.exception.NoActiveSubscriptionException;
import ru.panyukovnn.rfopenllmbillingmanager.model.ApiKey;
import ru.panyukovnn.rfopenllmbillingmanager.repository.ApiKeyRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.ApiKeyService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.referencemodelstarter.exception.CriticalException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final int KEY_PREFIX_LENGTH = 8;

    private final ApiKeyRepository apiKeyRepository;
    private final LitellmClient litellmClient;
    private final UserSubscriptionService userSubscriptionService;

    @Override
    @Transactional
    public CreateApiKeyResponse generateKey(UUID userId, String keyName) {
        validateActiveSubscription(userId);

        LitellmKeyGenerateResponse litellmResponse = requestLitellmKeyGeneration(userId);
        String generatedKey = litellmResponse.getKey();

        ApiKey savedKey = saveApiKey(userId, keyName, generatedKey, litellmResponse.getKeyId(), generatedKey);

        log.info("API-ключ {} создан для пользователя {}", savedKey.getId(), userId);

        return CreateApiKeyResponse.builder()
                .id(savedKey.getId())
                .name(savedKey.getName())
                .key(generatedKey)
                .build();
    }

    @Override
    @Transactional
    public void revokeKey(UUID userId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new BusinessException(
                        "d3b4",
                        "API-ключ не найден"));

        if (!userId.equals(apiKey.getAppUserId())) {
            throw new BusinessException(
                    "e5c6",
                    "Доступ к данному API-ключу запрещён");
        }

        litellmClient.revokeKey(apiKey.getLitellmKeyId());

        apiKey.setActive(false);
        apiKey.setRevokedAt(Instant.now());
        apiKeyRepository.save(apiKey);

        log.info("API-ключ {} отозван пользователем {}", keyId, userId);
    }

    @Override
    public String findActiveVirtualKey(UUID userId) {
        ApiKey apiKey = apiKeyRepository.findFirstByAppUserIdAndActiveTrueOrderByCreateTimeAsc(userId)
                .orElseThrow(() -> new BusinessException(
                        "k001",
                        "Активный API-ключ не найден"));

        if (apiKey.getVirtualKey() == null) {
            throw new BusinessException(
                    "k002",
                    "Активный API-ключ не содержит virtualKey");
        }

        return apiKey.getVirtualKey();
    }

    @Override
    public List<ApiKeyResponse> findUserKeys(UUID userId) {
        List<ApiKey> activeKeys = apiKeyRepository.findAllByAppUserIdAndActiveTrue(userId);

        return activeKeys.stream()
                .map(this::toApiKeyResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deactivateAllUserKeys(UUID userId) {
        List<ApiKey> activeKeys = apiKeyRepository.findAllByAppUserIdAndActiveTrue(userId);

        for (ApiKey apiKey : activeKeys) {
            litellmClient.revokeKey(apiKey.getLitellmKeyId());

            apiKey.setActive(false);
            apiKey.setRevokedAt(Instant.now());
            apiKeyRepository.save(apiKey);
        }

        log.info("Все API-ключи ({}) деактивированы для пользователя {}",
                activeKeys.size(), userId);
    }

    /**
     * Проверяет наличие активной подписки у пользователя
     */
    private void validateActiveSubscription(UUID userId) {
        boolean hasSubscription = userSubscriptionService.findActiveSubscription(userId).isPresent();

        if (!hasSubscription) {
            throw new NoActiveSubscriptionException(
                    "f1a2",
                    "Для генерации API-ключа необходима активная подписка");
        }
    }

    /**
     * Отправляет запрос на генерацию ключа в LiteLLM
     */
    private LitellmKeyGenerateResponse requestLitellmKeyGeneration(UUID userId) {
        LitellmKeyGenerateRequest litellmRequest = LitellmKeyGenerateRequest.builder()
                .userId(userId.toString())
                .metadata(Map.of("billing_user_id", userId.toString()))
                .build();

        return litellmClient.generateKey(litellmRequest);
    }

    /**
     * Сохраняет API-ключ в базу данных
     */
    private ApiKey saveApiKey(UUID userId, String keyName, String generatedKey, String litellmKeyId, String virtualKey) {
        String keyHash = hashKey(generatedKey);

        ApiKey apiKey = ApiKey.builder()
                .appUserId(userId)
                .keyHash(keyHash)
                .litellmKeyId(litellmKeyId)
                .virtualKey(virtualKey)
                .name(keyName)
                .active(true)
                .build();

        return apiKeyRepository.save(apiKey);
    }

    private ApiKeyResponse toApiKeyResponse(ApiKey apiKey) {
        String keyPrefix = apiKey.getKeyHash().substring(0, KEY_PREFIX_LENGTH);

        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .name(apiKey.getName())
                .keyPrefix(keyPrefix)
                .active(apiKey.getActive())
                .createdAt(apiKey.getCreateTime())
                .build();
    }

    private String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CriticalException("a7b3", "SHA-256 алгоритм недоступен", e.getMessage(), e);
        }
    }
}
