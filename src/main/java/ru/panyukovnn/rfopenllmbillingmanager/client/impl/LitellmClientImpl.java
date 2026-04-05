package ru.panyukovnn.rfopenllmbillingmanager.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyDeleteRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateBody;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateRequest;
import ru.panyukovnn.referencemodelstarter.util.RestCallWrapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LitellmClientImpl implements LitellmClient {

    private static final String LITE_LLM_SERVICE_NAME = "LiteLLM";

    private final RestClient liteLlmRestClient;

    @Override
    public LitellmKeyGenerateResponse generateKey(LitellmKeyGenerateRequest request) {
        log.info("Запрос на генерацию ключа в LiteLLM для пользователя {}", request.getUserId());

        return RestCallWrapper.executeRequest(
                () -> liteLlmRestClient.post()
                        .uri("/key/generate")
                        .body(request)
                        .retrieve()
                        .body(LitellmKeyGenerateResponse.class),
                LITE_LLM_SERVICE_NAME
        );
    }

    @Override
    public void revokeKey(String litellmKeyId) {
        log.info("Запрос на отзыв ключа {} в LiteLLM", litellmKeyId);

        LitellmKeyDeleteRequest deleteRequest = LitellmKeyDeleteRequest.builder()
                .keys(List.of(litellmKeyId))
                .build();

        RestCallWrapper.executeRequest(
                () -> liteLlmRestClient.post()
                        .uri("/key/delete")
                        .body(deleteRequest)
                        .retrieve()
                        .toBodilessEntity(),
                LITE_LLM_SERVICE_NAME
        );
    }

    @Override
    public void updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest request) {
        log.info("Запрос на обновление бюджета ключа {} в LiteLLM", litellmKeyId);

        LitellmKeyUpdateBody updateBody = LitellmKeyUpdateBody.builder()
                .key(litellmKeyId)
                .maxBudget(request.getMaxBudget())
                .tpmLimit(request.getTpmLimit())
                .build();

        RestCallWrapper.executeRequest(
                () -> liteLlmRestClient.post()
                        .uri("/key/update")
                        .body(updateBody)
                        .retrieve()
                        .toBodilessEntity(),
                LITE_LLM_SERVICE_NAME
        );
    }
}
