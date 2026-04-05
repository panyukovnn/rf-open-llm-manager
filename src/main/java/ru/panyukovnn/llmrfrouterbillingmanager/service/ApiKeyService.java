package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    CreateApiKeyResponse generateKey(UUID userId, String keyName);

    void revokeKey(UUID userId, UUID keyId);

    List<ApiKeyResponse> findUserKeys(UUID userId);

    void deactivateAllUserKeys(UUID userId);
}
