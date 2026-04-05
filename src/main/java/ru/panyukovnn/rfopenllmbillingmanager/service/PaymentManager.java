package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentManager {

    InitiatePaymentResponse handleInitiatePayment(UUID subscriptionPlanId);

    List<PaymentResponse> handleFindUserPayments();
}
