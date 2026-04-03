package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.CreateSubscriptionRequest;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserSubscriptionResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    @GetMapping("/current")
    public CommonResponse<UserSubscriptionResponse> findCurrentSubscription() {
        UserSubscriptionResponse subscription = userSubscriptionService.findCurrentSubscriptionResponse();

        return CommonResponse.<UserSubscriptionResponse>builder()
                .data(subscription)
                .build();
    }

    @PostMapping
    public CommonResponse<UserSubscriptionResponse> createSubscription(
            @Valid @RequestBody CommonRequest<CreateSubscriptionRequest> request) {
        UserSubscriptionResponse subscription = userSubscriptionService
                .activateCurrentSubscription(request.getData().getPlanId());

        return CommonResponse.<UserSubscriptionResponse>builder()
                .data(subscription)
                .build();
    }
}
