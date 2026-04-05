package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyManager {

    CreateApiKeyResponse handleGenerateKey(String keyName);

    List<ApiKeyResponse> handleFindUserKeys();

    void handleRevokeKey(UUID keyId);
}
