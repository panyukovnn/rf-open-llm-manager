package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserSubscriptionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.exception.TokenLimitExceededException;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.SubscriptionMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;
import ru.panyukovnn.rfopenllmbillingmanager.property.SubscriptionProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.UserSubscriptionRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;

import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionProperty subscriptionProperty;
    private final AppUserService appUserService;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public Optional<UserSubscription> findActiveSubscription(UUID userId) {
        return userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    @Nullable
    @Override
    public UserSubscriptionResponse findCurrentSubscriptionResponse() {
        AppUser currentUser = appUserService.findCurrentUser();

        return findActiveSubscription(currentUser.getId())
                .map(subscriptionMapper::toUserSubscriptionResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public UserSubscriptionResponse activateCurrentSubscription(UUID planId) {
        AppUser currentUser = appUserService.findCurrentUser();
        UserSubscription subscription = activateSubscription(currentUser.getId(), planId);

        return subscriptionMapper.toUserSubscriptionResponse(subscription);
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
                .orElseThrow(() -> new BusinessException(
                        "c9d1",
                        "Активная подписка не найдена"));

        SubscriptionPlan plan = subscriptionPlanService.findById(subscription.getSubscriptionPlanId());
        long newTokensUsed = subscription.getTokensUsed() + tokens;

        if (newTokensUsed > plan.getMonthlyTokenLimit()) {
            throw new TokenLimitExceededException(
                    "a3f7",
                    "Превышен лимит токенов");
        }

        subscription.setTokensUsed(newTokensUsed);
        userSubscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public int expireSubscriptions() {
        List<UserSubscription> expiredSubscriptions = userSubscriptionRepository
                .findAllByStatusAndPeriodEndBefore(SubscriptionStatus.ACTIVE, Instant.now());

        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);

            log.info("Подписка {} пользователя {} переведена в статус EXPIRED",
                    subscription.getId(), subscription.getAppUserId());
        }

        return expiredSubscriptions.size();
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
