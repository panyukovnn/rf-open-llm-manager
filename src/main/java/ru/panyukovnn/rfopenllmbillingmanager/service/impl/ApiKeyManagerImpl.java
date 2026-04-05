package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ApiKeyResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateApiKeyResponse;
import ru.panyukovnn.rfopenllmbillingmanager.service.ApiKeyManager;
import ru.panyukovnn.rfopenllmbillingmanager.service.ApiKeyService;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyManagerImpl implements ApiKeyManager {

    private final ApiKeyService apiKeyService;
    private final AppUserService appUserService;

    @Override
    public CreateApiKeyResponse handleGenerateKey(String keyName) {
        UUID userId = appUserService.findCurrentUser().getId();

        return apiKeyService.generateKey(userId, keyName);
    }

    @Override
    public List<ApiKeyResponse> handleFindUserKeys() {
        UUID userId = appUserService.findCurrentUser().getId();

        return apiKeyService.findUserKeys(userId);
    }

    @Override
    public void handleRevokeKey(UUID keyId) {
        UUID userId = appUserService.findCurrentUser().getId();
        apiKeyService.revokeKey(userId, keyId);
    }
}