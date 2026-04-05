package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.rfopenllmbillingmanager.service.PaymentManager;
import ru.panyukovnn.rfopenllmbillingmanager.service.PaymentService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentManagerImpl implements PaymentManager {

    private final PaymentService paymentService;
    private final AppUserService appUserService;

    @Override
    public InitiatePaymentResponse handleInitiatePayment(UUID subscriptionPlanId) {
        UUID userId = appUserService.findCurrentUser().getId();

        return paymentService.initiatePayment(userId, subscriptionPlanId);
    }

    @Override
    public List<PaymentResponse> handleFindUserPayments() {
        UUID userId = appUserService.findCurrentUser().getId();

        return paymentService.findUserPayments(userId);
    }
}
