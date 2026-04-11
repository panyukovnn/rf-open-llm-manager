package ru.panyukovnn.rfopenllmbillingmanager.client;

import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateRequest;

import java.util.function.Consumer;

public interface LitellmClient {

    LitellmKeyGenerateResponse generateKey(LitellmKeyGenerateRequest request);

    void revokeKey(String litellmKeyId);

    void updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest request);

    void streamCompletion(String virtualKey, ChatCompletionRequest request, Consumer<ChatCompletionChunk> chunkConsumer);

    String chatCompletion(String virtualKey, ChatCompletionRequest request);
}
