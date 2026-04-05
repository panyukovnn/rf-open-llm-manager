package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.InitiatePaymentRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.InitiatePaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.PaymentResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.PaymentService;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AppUserService appUserService;

    @PostMapping
    public CommonResponse<InitiatePaymentResponse> initiatePayment(
            @Valid @RequestBody CommonRequest<InitiatePaymentRequest> request) {
        AppUser currentUser = appUserService.findCurrentUser();
        InitiatePaymentResponse response = paymentService
                .initiatePayment(currentUser.getId(), request.getData().getSubscriptionPlanId());

        return CommonResponse.<InitiatePaymentResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping
    public CommonResponse<CommonItemsResponse<PaymentResponse>> findUserPayments() {
        AppUser currentUser = appUserService.findCurrentUser();
        List<PaymentResponse> payments = paymentService.findUserPayments(currentUser.getId());
        CommonItemsResponse<PaymentResponse> items = CommonItemsResponse.<PaymentResponse>builder()
                .items(payments)
                .itemsCount(payments.size())
                .totalCount(payments.size())
                .build();

        return CommonResponse.<CommonItemsResponse<PaymentResponse>>builder()
                .data(items)
                .build();
    }
}
