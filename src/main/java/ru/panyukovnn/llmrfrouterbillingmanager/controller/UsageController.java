package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageEventResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageSummaryResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.SubscriptionPlanService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UsageTrackingService;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageTrackingService usageTrackingService;
    private final AppUserService appUserService;
    private final UserSubscriptionService userSubscriptionService;
    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public CommonResponse<UsageSummaryResponse> findUsageSummary(
            @RequestParam Instant from,
            @RequestParam Instant to) {
        UUID userId = appUserService.findCurrentUser().getId();
        List<UsageEventResponse> events = usageTrackingService.findUsageHistory(userId, from, to);

        UsageSummaryResponse summary = buildSummary(userId, events);

        return CommonResponse.<UsageSummaryResponse>builder()
                .data(summary)
                .build();
    }

    private UsageSummaryResponse buildSummary(UUID userId, List<UsageEventResponse> events) {
        long totalTokensUsed = events.stream()
                .mapToLong(event -> Optional.ofNullable(event.getTotalTokens()).orElse(0L))
                .sum();

        Map<String, Long> usageByModel = events.stream()
                .collect(Collectors.groupingBy(
                        UsageEventResponse::getModel,
                        Collectors.summingLong(event -> Optional.ofNullable(event.getTotalTokens()).orElse(0L))));

        Long tokenLimit = resolveTokenLimit(userId);

        return UsageSummaryResponse.builder()
                .totalTokensUsed(totalTokensUsed)
                .tokenLimit(tokenLimit)
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
