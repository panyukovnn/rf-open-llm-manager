package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageEventResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageSummaryResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UsageManager;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UsageTrackingService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageManagerImpl implements UsageManager {

    private final UsageTrackingService usageTrackingService;
    private final AppUserService appUserService;
    private final UserSubscriptionService userSubscriptionService;
    private final SubscriptionPlanService subscriptionPlanService;

    @Override
    public UsageSummaryResponse handleFindCurrentUserUsageSummary(Instant from, Instant to) {
        UUID userId = appUserService.findCurrentUser().getId();
        List<UsageEventResponse> events = usageTrackingService.findUsageHistory(userId, from, to);

        return buildSummary(userId, events);
    }

    private UsageSummaryResponse buildSummary(UUID userId, List<UsageEventResponse> events) {
        long totalTokensUsed = events.stream()
                .mapToLong(event -> Optional.ofNullable(event.getTotalTokens()).orElse(0L))
                .sum();

        Map<String, Long> usageByModel = events.stream()
                .collect(Collectors.groupingBy(
                        UsageEventResponse::getModel,
                        Collectors.summingLong(event -> Optional.ofNullable(event.getTotalTokens()).orElse(0L))));

        return UsageSummaryResponse.builder()
                .totalTokensUsed(totalTokensUsed)
                .tokenLimit(resolveTokenLimit(userId))
                .usageByModel(usageByModel)
                .build();
    }

    private Long resolveTokenLimit(UUID userId) {
        Optional<UserSubscription> subscription = userSubscriptionService.findActiveSubscription(userId);

        if (subscription.isEmpty()) {
            return 0L;
        }
        SubscriptionPlan plan = subscriptionPlanService.findById(subscription.get().getSubscriptionPlanId());

        return plan.getMonthlyTokenLimit();
    }
}
