package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UsageEventResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UsageTrackingService {

    void processUsageCallback(List<LitellmUsageCallbackPayload> payloads);

    List<UsageEventResponse> findUsageHistory(UUID userId, Instant from, Instant to);
}
