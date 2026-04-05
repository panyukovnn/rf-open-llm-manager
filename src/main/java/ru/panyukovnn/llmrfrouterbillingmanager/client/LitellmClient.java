package ru.panyukovnn.llmrfrouterbillingmanager.client;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyUpdateRequest;

public interface LitellmClient {

    LitellmKeyGenerateResponse generateKey(LitellmKeyGenerateRequest request);

    void revokeKey(String litellmKeyId);

    void updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest request);
}
