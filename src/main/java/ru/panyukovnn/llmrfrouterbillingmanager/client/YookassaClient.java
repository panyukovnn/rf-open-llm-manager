package ru.panyukovnn.llmrfrouterbillingmanager.client;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentResponse;

public interface YookassaClient {

    YookassaCreatePaymentResponse createPayment(YookassaCreatePaymentRequest request);

    YookassaCreatePaymentResponse getPaymentInfo(String yookassaPaymentId);
}