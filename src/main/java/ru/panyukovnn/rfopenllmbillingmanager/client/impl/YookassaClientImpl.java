package ru.panyukovnn.rfopenllmbillingmanager.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.rfopenllmbillingmanager.client.YookassaClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa.YookassaAmount;
import ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa.YookassaConfirmationRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa.YookassaPaymentApiResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa.YookassaPaymentBody;
import ru.panyukovnn.referencemodelstarter.exception.CriticalException;
import ru.panyukovnn.referencemodelstarter.util.RestCallWrapper;

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
                .amount(new YookassaAmount(request.getAmountValue(), request.getAmountCurrency()))
                .confirmation(new YookassaConfirmationRequest(CONFIRMATION_TYPE_REDIRECT, request.getReturnUrl()))
                .capture(true)
                .description(request.getDescription())
                .metadata(request.getMetadata())
                .build();
    }

    private YookassaCreatePaymentResponse toResponse(YookassaPaymentApiResponse response) {
        if (response == null) {
            throw new CriticalException("y3c1", "Пустой ответ от ЮKassa");
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
}
