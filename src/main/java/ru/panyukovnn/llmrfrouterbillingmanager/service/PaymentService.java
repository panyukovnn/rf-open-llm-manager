package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaWebhookPayload;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    InitiatePaymentResponse initiatePayment(UUID userId, UUID planId);

    void processWebhook(YookassaWebhookPayload payload);

    List<PaymentResponse> findUserPayments(UUID userId);
}
