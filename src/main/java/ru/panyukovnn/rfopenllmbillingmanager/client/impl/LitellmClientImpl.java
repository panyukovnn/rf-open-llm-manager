package ru.panyukovnn.rfopenllmbillingmanager.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.referencemodelstarter.util.RestCallWrapper;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyDeleteRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyGenerateResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateBody;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmKeyUpdateRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LitellmClientImpl implements LitellmClient {

    private static final String LITE_LLM_SERVICE_NAME = "LiteLLM";
    private static final String SSE_DATA_PREFIX = "data: ";
    private static final String SSE_DONE_MARKER = "[DONE]";
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_PAYMENT_REQUIRED = 402;

    private final RestClient liteLlmRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Override
    public Iterator<ChatCompletionChunk> streamCompletion(String virtualKey, ChatCompletionRequest request) {
        log.info("Запрос на стриминг chat completion в LiteLLM, model={}", request.getModel());

        try {
            return executeStreamCall(virtualKey, toStreamRequest(request));
        } catch (BusinessException e) {
            throw e;
        } catch (ResourceAccessException e) {
            log.error("Ошибка соединения с LiteLLM: {}", e.getMessage(), e);

            throw new BusinessException("ad65", "LLM-апстрим недоступен", e);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при вызове LiteLLM: {}", e.getMessage(), e);

            throw new BusinessException("453e", "LLM-апстрим недоступен", e);
        }
    }

    /**
     * Выполняет stream-запрос и возвращает итератор распарсенных chunk-ов
     */
    private Iterator<ChatCompletionChunk> executeStreamCall(String virtualKey, ChatCompletionRequest streamRequest) {
        return liteLlmRestClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + virtualKey))
                .body(streamRequest)
                .exchange((httpRequest, httpResponse) -> {
                    HttpStatusCode status = httpResponse.getStatusCode();

                    if (status.isError()) {
                        throw toStatusException(status, readBodySafely(httpResponse.getBody()));
                    }

                    return parseSseChunks(httpResponse.getBody()).iterator();
                });
    }

    /**
     * Строит запрос с принудительно включённым stream=true
     */
    private ChatCompletionRequest toStreamRequest(ChatCompletionRequest source) {
        return ChatCompletionRequest.builder()
                .model(source.getModel())
                .messages(source.getMessages())
                .temperature(source.getTemperature())
                .maxTokens(source.getMaxTokens())
                .stream(Boolean.TRUE)
                .build();
    }

    /**
     * Сопоставляет HTTP-статус ответа LiteLLM с доменным BusinessException
     */
    private BusinessException toStatusException(HttpStatusCode status, String body) {
        log.warn("Ошибка ответа от LiteLLM: status={}, body='{}'", status, body);

        if (status.value() == HTTP_UNAUTHORIZED) {
            return new BusinessException("8d4a", "Ключ LiteLLM отклонён");
        }

        if (status.value() == HTTP_PAYMENT_REQUIRED) {
            return new BusinessException("f569", "Бюджет подписки исчерпан");
        }

        return new BusinessException("d9ac", "LLM-апстрим недоступен");
    }

    /**
     * Читает тело ответа безопасно — используется только для логирования ошибок
     */
    private String readBodySafely(InputStream body) {
        try {
            byte[] bytes = body.readAllBytes();

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Парсит SSE-поток в список chunk-ов, учитывая маркер окончания [DONE]
     */
    private List<ChatCompletionChunk> parseSseChunks(InputStream body) throws IOException {
        List<ChatCompletionChunk> chunks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            while (line != null) {
                if (line.startsWith(SSE_DATA_PREFIX)) {
                    String data = line.substring(SSE_DATA_PREFIX.length());

                    if (SSE_DONE_MARKER.equals(data)) {
                        break;
                    }

                    chunks.add(objectMapper.readValue(data, ChatCompletionChunk.class));
                }

                line = reader.readLine();
            }
        }

        return chunks;
    }
}