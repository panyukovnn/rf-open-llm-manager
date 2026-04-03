package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.llmrfrouterbillingmanager.exception.TokenLimitExceededException;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.UserSubscriptionRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;

import ru.panyukovnn.llmrfrouterbillingmanager.property.SubscriptionProperty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionProperty subscriptionProperty;

    @Override
    public Optional<UserSubscription> findActiveSubscription(UUID userId) {
        return userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    @Override
    @Transactional
    public UserSubscription activateSubscription(UUID userId, UUID planId) {
        SubscriptionPlan plan = subscriptionPlanService.findById(planId);

        Instant now = Instant.now();
        Instant periodEnd = now.plus(subscriptionProperty.getPeriodDays(), ChronoUnit.DAYS);

        UserSubscription subscription = UserSubscription.builder()
                .appUserId(userId)
                .subscriptionPlanId(plan.getId())
                .status(SubscriptionStatus.ACTIVE)
                .tokensUsed(0L)
                .periodStart(now)
                .periodEnd(periodEnd)
                .build();

        return userSubscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void deductTokens(UUID userId, long tokens) {
        UserSubscription subscription = userSubscriptionRepository
                .findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException(
                        "Активная подписка не найдена для пользователя: " + userId));

        SubscriptionPlan plan = subscriptionPlanService.findById(subscription.getSubscriptionPlanId());
        long newTokensUsed = subscription.getTokensUsed() + tokens;

        if (newTokensUsed > plan.getMonthlyTokenLimit()) {
            throw new TokenLimitExceededException(
                    "Превышен лимит токенов для пользователя: " + userId
                            + ", использовано: " + newTokensUsed
                            + ", лимит: " + plan.getMonthlyTokenLimit());
        }

        subscription.setTokensUsed(newTokensUsed);
        userSubscriptionRepository.save(subscription);
    }

    @Override
    public boolean hasAvailableTokens(UUID userId) {
        Optional<UserSubscription> subscriptionOpt = userSubscriptionRepository
                .findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        if (subscriptionOpt.isEmpty()) {
            return false;
        }

        UserSubscription subscription = subscriptionOpt.get();
        SubscriptionPlan plan = subscriptionPlanService.findById(subscription.getSubscriptionPlanId());

        return subscription.getTokensUsed() < plan.getMonthlyTokenLimit();
    }
}
