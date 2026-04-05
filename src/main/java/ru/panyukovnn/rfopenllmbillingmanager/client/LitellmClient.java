package ru.panyukovnn.rfopenllmbillingmanager.client;

import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateRequest;

public interface LitellmClient {

    LitellmKeyGenerateResponse generateKey(LitellmKeyGenerateRequest request);

    void revokeKey(String litellmKeyId);

    void updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest request);
}
