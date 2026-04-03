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
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.SubscriptionMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

@RestController
@RequestMapping("/billing-manager/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final AppUserService appUserService;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping("/current")
    public CommonResponse<UserSubscriptionResponse> findCurrentSubscription() {
        AppUser currentUser = appUserService.findCurrentUser();
        UserSubscriptionResponse subscriptionResponse = userSubscriptionService
                .findActiveSubscription(currentUser.getId())
                .map(subscriptionMapper::toUserSubscriptionResponse)
                .orElse(null);

        return CommonResponse.<UserSubscriptionResponse>builder()
                .data(subscriptionResponse)
                .build();
    }

    @PostMapping
    public CommonResponse<UserSubscriptionResponse> createSubscription(
            @Valid @RequestBody CommonRequest<CreateSubscriptionRequest> request) {
        AppUser currentUser = appUserService.findCurrentUser();
        UserSubscription subscription = userSubscriptionService
                .activateSubscription(currentUser.getId(), request.getData().getPlanId());
        UserSubscriptionResponse subscriptionResponse = subscriptionMapper.toUserSubscriptionResponse(subscription);

        return CommonResponse.<UserSubscriptionResponse>builder()
                .data(subscriptionResponse)
                .build();
    }
}