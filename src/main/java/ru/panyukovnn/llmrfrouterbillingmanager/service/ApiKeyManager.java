package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyManager {

    CreateApiKeyResponse handleGenerateKey(String keyName);

    List<ApiKeyResponse> handleFindUserKeys();

    void handleRevokeKey(UUID keyId);
}
