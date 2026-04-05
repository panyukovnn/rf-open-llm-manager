package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UsageEventResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UsageTrackingService {

    void processUsageCallback(List<LitellmUsageCallbackPayload> payloads);

    List<UsageEventResponse> findUsageHistory(UUID userId, Instant from, Instant to);
}
