package ru.panyukovnn.llmrfrouterbillingmanager.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.llmrfrouterbillingmanager.client.LitellmClient;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmKeyUpdateRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.property.LitellmProperty;
import ru.panyukovnn.referencemodelstarter.util.RestCallWrapper;

import java.util.Map;

@Slf4j
@Component
public class LitellmClientImpl implements LitellmClient {

    private static final String SERVICE_NAME = "LiteLLM";

    private final RestClient restClient;

    public LitellmClientImpl(LitellmProperty litellmProperty) {
        this.restClient = RestClient.builder()
                .baseUrl(litellmProperty.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + litellmProperty.getMasterKey())
                .build();
    }

    @Override
    public LitellmKeyGenerateResponse generateKey(LitellmKeyGenerateRequest request) {
        log.info("Запрос на генерацию ключа в LiteLLM для пользователя {}", request.getUserId());

        return RestCallWrapper.executeRequest(
                () -> restClient.post()
                        .uri("/key/generate")
                        .body(request)
                        .retrieve()
                        .body(LitellmKeyGenerateResponse.class),
                SERVICE_NAME
        );
    }

    @Override
    public void revokeKey(String litellmKeyId) {
        log.info("Запрос на отзыв ключа {} в LiteLLM", litellmKeyId);

        RestCallWrapper.executeRequest(
                () -> restClient.post()
                        .uri("/key/delete")
                        .body(Map.of("keys", new String[]{litellmKeyId}))
                        .retrieve()
                        .toBodilessEntity(),
                SERVICE_NAME
        );
    }

    @Override
    public void updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest request) {
        log.info("Запрос на обновление бюджета ключа {} в LiteLLM", litellmKeyId);

        RestCallWrapper.executeRequest(
                () -> restClient.post()
                        .uri("/key/update")
                        .body(Map.of(
                                "key", litellmKeyId,
                                "max_budget", request.getMaxBudget(),
                                "tpm_limit", request.getTpmLimit()
                        ))
                        .retrieve()
                        .toBodilessEntity(),
                SERVICE_NAME
        );
    }
}
