package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.UsageSummaryResponse;

import java.time.Instant;

public interface UsageManager {

    UsageSummaryResponse handleFindCurrentUserUsageSummary(Instant from, Instant to);
}
