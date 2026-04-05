package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.UserSubscriptionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;

import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionService {

    Optional<UserSubscription> findActiveSubscription(UUID userId);

    UserSubscriptionResponse findCurrentSubscriptionResponse();

    UserSubscriptionResponse activateCurrentSubscription(UUID planId);

    UserSubscription activateSubscription(UUID userId, UUID planId);

    void deductTokens(UUID userId, long tokens);

    int expireSubscriptions();

    boolean hasAvailableTokens(UUID userId);
}
