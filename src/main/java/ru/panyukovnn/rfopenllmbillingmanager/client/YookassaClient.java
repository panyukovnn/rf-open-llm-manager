package ru.panyukovnn.rfopenllmbillingmanager.client;

import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentResponse;

public interface YookassaClient {

    YookassaCreatePaymentResponse createPayment(YookassaCreatePaymentRequest request);

    YookassaCreatePaymentResponse getPaymentInfo(String yookassaPaymentId);
}