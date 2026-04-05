package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.client.YookassaClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaCreatePaymentResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaWebhookPayload;
import ru.panyukovnn.rfopenllmbillingmanager.model.Payment;
import ru.panyukovnn.rfopenllmbillingmanager.model.PaymentStatus;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.rfopenllmbillingmanager.property.YookassaProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.PaymentRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.PaymentService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String CURRENCY_RUB = "RUB";
    private static final String EVENT_PAYMENT_SUCCEEDED = "payment.succeeded";
    private static final String EVENT_PAYMENT_CANCELED = "payment.canceled";
    private static final String METADATA_PAYMENT_ID = "paymentId";
    private static final int KOPECKS_IN_RUBLE = 100;
    private static final String PAYMENT_DESCRIPTION_TEMPLATE = "Оплата тарифа %s";

    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserSubscriptionService userSubscriptionService;
    private final YookassaClient yookassaClient;
    private final YookassaProperty yookassaProperty;

    @Override
    public InitiatePaymentResponse initiatePayment(UUID userId, UUID planId) {
        SubscriptionPlan plan = subscriptionPlanService.findById(planId);
        Payment payment = paymentRepository.save(Payment.builder()
                .appUserId(userId)
                .subscriptionPlanId(plan.getId())
                .amountKopecks(plan.getPriceKopecks())
                .status(PaymentStatus.PENDING)
                .build());

        YookassaCreatePaymentResponse yookassaResponse = callYookassa(plan, payment);

        payment.setYookassaPaymentId(yookassaResponse.getId());
        paymentRepository.save(payment);

        return InitiatePaymentResponse.builder()
                .paymentId(payment.getId())
                .confirmationUrl(yookassaResponse.getConfirmationUrl())
                .build();
    }

    @Override
    @Transactional
    public void processWebhook(YookassaWebhookPayload payload) {
        String yookassaPaymentId = payload.getObject().getId();
        Payment payment = paymentRepository.findByYookassaPaymentId(yookassaPaymentId)
                .orElseThrow(() -> new BusinessException(
                        "p1f4",
                        "Платёж не найден по yookassaPaymentId " + yookassaPaymentId));

        String event = payload.getEvent();

        if (EVENT_PAYMENT_SUCCEEDED.equals(event)) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepository.save(payment);
            userSubscriptionService.activateSubscription(payment.getAppUserId(), payment.getSubscriptionPlanId());
            log.info("Платёж {} успешно обработан, подписка активирована", payment.getId());
        } else if (EVENT_PAYMENT_CANCELED.equals(event)) {
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
            log.info("Платёж {} отменён", payment.getId());
        } else {
            log.warn("Получено неподдерживаемое событие ЮKassa {} для платежа {}", event, payment.getId());
        }
    }

    @Override
    public List<PaymentResponse> findUserPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findAllByAppUserId(userId);

        return payments.stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    private YookassaCreatePaymentResponse callYookassa(SubscriptionPlan plan, Payment payment) {
        String amountValue = BigDecimal.valueOf(plan.getPriceKopecks())
                .divide(BigDecimal.valueOf(KOPECKS_IN_RUBLE), 2, RoundingMode.HALF_UP)
                .toPlainString();
        YookassaCreatePaymentRequest request = YookassaCreatePaymentRequest.builder()
                .amountValue(amountValue)
                .amountCurrency(CURRENCY_RUB)
                .description(String.format(PAYMENT_DESCRIPTION_TEMPLATE, plan.getName()))
                .returnUrl(yookassaProperty.getReturnUrl())
                .metadata(Map.of(METADATA_PAYMENT_ID, payment.getId().toString()))
                .build();

        return yookassaClient.createPayment(request);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        SubscriptionPlan plan = subscriptionPlanService.findById(payment.getSubscriptionPlanId());

        return PaymentResponse.builder()
                .id(payment.getId())
                .planName(plan.getName())
                .amountKopecks(payment.getAmountKopecks())
                .status(payment.getStatus())
                .createdAt(payment.getCreateTime())
                .build();
    }
}
