package ru.panyukovnn.llmrfrouterbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.llmrfrouterbillingmanager.client.YookassaClient;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaCreatePaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.YookassaWebhookPayload;
import ru.panyukovnn.llmrfrouterbillingmanager.model.Payment;
import ru.panyukovnn.llmrfrouterbillingmanager.model.PaymentStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.property.YookassaProperty;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.PaymentRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.impl.PaymentServiceImpl;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplUnitTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private SubscriptionPlanService subscriptionPlanService;
    @Mock
    private UserSubscriptionService userSubscriptionService;
    @Mock
    private YookassaClient yookassaClient;
    @Mock
    private YookassaProperty yookassaProperty;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Nested
    class InitiatePayment {

        @Test
        void when_initiatePayment_then_paymentCreatedAndConfirmationUrlReturned() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .name("Standard")
                    .priceKopecks(50000L)
                    .build();

            when(subscriptionPlanService.findById(planId)).thenReturn(plan);
            when(yookassaProperty.getReturnUrl()).thenReturn("http://localhost/return");
            when(paymentRepository.save(any(Payment.class)))
                    .thenAnswer(invocation -> {
                        Payment saved = invocation.getArgument(0);

                        if (saved.getId() == null) {
                            saved.setId(UUID.randomUUID());
                        }

                        return saved;
                    });
            when(yookassaClient.createPayment(any(YookassaCreatePaymentRequest.class)))
                    .thenReturn(YookassaCreatePaymentResponse.builder()
                            .id("yk-123")
                            .status("pending")
                            .confirmationUrl("http://pay.yookassa/abc")
                            .build());

            InitiatePaymentResponse result = paymentService.initiatePayment(userId, planId);

            assertNotNull(result);
            assertNotNull(result.getPaymentId());
            assertEquals("http://pay.yookassa/abc", result.getConfirmationUrl());
            verify(paymentRepository, org.mockito.Mockito.times(2)).save(any(Payment.class));
        }

        @Test
        void when_initiatePayment_withInactivePlan_then_subscriptionPlanNotFoundException() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();

            when(subscriptionPlanService.findById(planId))
                    .thenThrow(new BusinessException("e7a3", "Тарифный план не найден"));

            assertThrows(BusinessException.class,
                    () -> paymentService.initiatePayment(userId, planId));
            verify(paymentRepository, never()).save(any());
            verify(yookassaClient, never()).createPayment(any());
        }
    }

    @Nested
    class ProcessWebhook {

        @Test
        void when_processWebhook_withSucceededStatus_then_subscriptionActivated() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .subscriptionPlanId(planId)
                    .yookassaPaymentId("yk-123")
                    .status(PaymentStatus.PENDING)
                    .build();
            YookassaWebhookPayload payload = YookassaWebhookPayload.builder()
                    .event("payment.succeeded")
                    .object(YookassaWebhookPayload.PaymentObject.builder()
                            .id("yk-123")
                            .status("succeeded")
                            .build())
                    .build();

            when(paymentRepository.findByYookassaPaymentId("yk-123")).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            paymentService.processWebhook(payload);

            assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());
            verify(userSubscriptionService).activateSubscription(userId, planId);
        }

        @Test
        void when_processWebhook_withCancelledStatus_then_paymentStatusUpdated() {
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID())
                    .appUserId(UUID.randomUUID())
                    .subscriptionPlanId(UUID.randomUUID())
                    .yookassaPaymentId("yk-456")
                    .status(PaymentStatus.PENDING)
                    .build();
            YookassaWebhookPayload payload = YookassaWebhookPayload.builder()
                    .event("payment.canceled")
                    .object(YookassaWebhookPayload.PaymentObject.builder()
                            .id("yk-456")
                            .status("canceled")
                            .build())
                    .build();

            when(paymentRepository.findByYookassaPaymentId("yk-456")).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            paymentService.processWebhook(payload);

            assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
            verify(userSubscriptionService, never()).activateSubscription(any(), any());
        }

        @Test
        void when_processWebhook_withUnknownPaymentId_then_paymentNotFoundException() {
            YookassaWebhookPayload payload = YookassaWebhookPayload.builder()
                    .event("payment.succeeded")
                    .object(YookassaWebhookPayload.PaymentObject.builder()
                            .id("yk-unknown")
                            .status("succeeded")
                            .build())
                    .build();

            when(paymentRepository.findByYookassaPaymentId("yk-unknown")).thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> paymentService.processWebhook(payload));
            verify(userSubscriptionService, never()).activateSubscription(any(), any());
        }
    }

    @Nested
    class FindUserPayments {

        @Test
        void when_findUserPayments_then_success() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .subscriptionPlanId(planId)
                    .amountKopecks(50000L)
                    .status(PaymentStatus.SUCCEEDED)
                    .build();
            payment.setCreateTime(Instant.now());
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .name("Standard")
                    .build();

            when(paymentRepository.findAllByAppUserId(userId)).thenReturn(List.of(payment));
            when(subscriptionPlanService.findById(planId)).thenReturn(plan);

            List<PaymentResponse> result = paymentService.findUserPayments(userId);

            assertEquals(1, result.size());
            PaymentResponse response = result.get(0);
            assertEquals(payment.getId(), response.getId());
            assertEquals("Standard", response.getPlanName());
            assertEquals(50000L, response.getAmountKopecks());
            assertEquals(PaymentStatus.SUCCEEDED, response.getStatus());
        }
    }
}
