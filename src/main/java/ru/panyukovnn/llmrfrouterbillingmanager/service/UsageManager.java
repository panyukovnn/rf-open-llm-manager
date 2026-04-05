package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageSummaryResponse;

import java.time.Instant;

public interface UsageManager {

    UsageSummaryResponse handleFindCurrentUserUsageSummary(Instant from, Instant to);
}
