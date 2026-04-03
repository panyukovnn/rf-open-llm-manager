package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;

import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionService {

    Optional<UserSubscription> findActiveSubscription(UUID userId);

    UserSubscription activateSubscription(UUID userId, UUID planId);

    void deductTokens(UUID userId, long tokens);

    boolean hasAvailableTokens(UUID userId);
}
