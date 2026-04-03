package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserSubscriptionService userSubscriptionService;
    private final AppUserService appUserService;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping("/current")
    public ResponseEntity<UserSubscriptionResponse> findCurrentSubscription() {
        AppUser currentUser = appUserService.findCurrentUser();
        Optional<UserSubscription> subscription = userSubscriptionService
                .findActiveSubscription(currentUser.getId());

        return subscription
                .map(subscriptionMapper::toUserSubscriptionResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public UserSubscriptionResponse createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        AppUser currentUser = appUserService.findCurrentUser();
        UserSubscription subscription = userSubscriptionService
                .activateSubscription(currentUser.getId(), request.getPlanId());

        return subscriptionMapper.toUserSubscriptionResponse(subscription);
    }
}
