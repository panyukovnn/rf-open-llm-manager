package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaWebhookPayload;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    InitiatePaymentResponse initiatePayment(UUID userId, UUID planId);

    void processWebhook(YookassaWebhookPayload payload);

    List<PaymentResponse> findUserPayments(UUID userId);
}
