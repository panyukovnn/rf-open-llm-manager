package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.AppUserMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;
import ru.panyukovnn.rfopenllmbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.rfopenllmbillingmanager.repository.UserSubscriptionRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanService subscriptionPlanService;

    @Override
    public AppUser findCurrentUser() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return appUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(
                        "b4e2",
                        "Пользователь не найден"));
    }

    @Override
    public UserProfileResponse findCurrentUserProfile() {
        AppUser currentUser = findCurrentUser();
        UserProfileResponse response = appUserMapper.toUserProfileResponse(currentUser);

        Optional<UserSubscription> activeSubscription = userSubscriptionRepository
                .findByAppUserIdAndStatus(currentUser.getId(), SubscriptionStatus.ACTIVE);

        activeSubscription.ifPresent(subscription -> {
            SubscriptionPlan plan = subscriptionPlanService.findById(subscription.getSubscriptionPlanId());

            response.setCurrentPlan(plan.getName());
            response.setTokensUsed(subscription.getTokensUsed());
            response.setTokenLimit(plan.getMonthlyTokenLimit());
        });

        return response;
    }

    @Override
    public Optional<AppUser> findByGoogleId(String googleId) {
        return appUserRepository.findByGoogleId(googleId);
    }
}
