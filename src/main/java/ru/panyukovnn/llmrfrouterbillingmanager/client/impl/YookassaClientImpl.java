package ru.panyukovnn.llmrfrouterbillingmanager.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.llmrfrouterbillingmanager.client.YookassaClient;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentResponse;
import ru.panyukovnn.referencemodelstarter.util.RestCallWrapper;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class YookassaClientImpl implements YookassaClient {

    private static final String YOOKASSA_SERVICE_NAME = "YooKassa";
    private static final String IDEMPOTENCE_KEY_HEADER = "Idempotence-Key";
    private static final String CONFIRMATION_TYPE_REDIRECT = "redirect";

    private final RestClient yookassaRestClient;

    @Override
    public YookassaCreatePaymentResponse createPayment(YookassaCreatePaymentRequest request) {
        log.info("Запрос на создание платежа в ЮKassa на сумму {}", request.getAmountValue());

        YookassaPaymentBody body = buildBody(request);
        String idempotenceKey = UUID.randomUUID().toString();

        YookassaPaymentApiResponse response = RestCallWrapper.executeRequest(
                () -> yookassaRestClient.post()
                        .uri("/payments")
                        .header(IDEMPOTENCE_KEY_HEADER, idempotenceKey)
                        .body(body)
                        .retrieve()
                        .body(YookassaPaymentApiResponse.class),
                YOOKASSA_SERVICE_NAME
        );

        return toResponse(response);
    }

    @Override
    public YookassaCreatePaymentResponse getPaymentInfo(String yookassaPaymentId) {
        log.info("Запрос информации о платеже {} в ЮKassa", yookassaPaymentId);

        YookassaPaymentApiResponse response = RestCallWrapper.executeRequest(
                () -> yookassaRestClient.get()
                        .uri("/payments/{id}", yookassaPaymentId)
                        .retrieve()
                        .body(YookassaPaymentApiResponse.class),
                YOOKASSA_SERVICE_NAME
        );

        return toResponse(response);
    }

    private YookassaPaymentBody buildBody(YookassaCreatePaymentRequest request) {
        return YookassaPaymentBody.builder()
                .amount(new Amount(request.getAmountValue(), request.getAmountCurrency()))
                .confirmation(new ConfirmationRequest(CONFIRMATION_TYPE_REDIRECT, request.getReturnUrl()))
                .capture(true)
                .description(request.getDescription())
                .metadata(request.getMetadata())
                .build();
    }

    private YookassaCreatePaymentResponse toResponse(YookassaPaymentApiResponse response) {
        if (response == null) {
            throw new IllegalStateException("Пустой ответ от ЮKassa");
        }
        String confirmationUrl = response.getConfirmation() != null
                ? response.getConfirmation().getConfirmationUrl()
                : null;

        return YookassaCreatePaymentResponse.builder()
                .id(response.getId())
                .status(response.getStatus())
                .confirmationUrl(confirmationUrl)
                .build();
    }

    @Getter
    @Builder
    static class YookassaPaymentBody {

        private final Amount amount;
        private final ConfirmationRequest confirmation;
        private final Boolean capture;
        private final String description;
        private final Map<String, String> metadata;
    }

    @Getter
    @AllArgsConstructor
    static class Amount {

        private final String value;
        private final String currency;
    }

    @Getter
    @AllArgsConstructor
    static class ConfirmationRequest {

        private final String type;
        @JsonProperty("return_url")
        private final String returnUrl;
    }

    @Getter
    @Setter
    static class YookassaPaymentApiResponse {

        private String id;
        private String status;
        private ConfirmationResponse confirmation;
    }

    @Getter
    @Setter
    static class ConfirmationResponse {

        private String type;
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
    }
}
