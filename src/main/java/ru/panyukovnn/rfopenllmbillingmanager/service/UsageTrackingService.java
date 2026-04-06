package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UsageEventResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UsageTrackingService {

    void processUsageCallback(List<LitellmUsageCallbackPayload> payloads);

    /**
     * Сохраняет assistant-сообщение и записывает событие использования в одной транзакции
     */
    Message recordChatUsage(UUID appUserId, UUID sessionId, Message assistantMessage);

    List<UsageEventResponse> findUsageHistory(UUID userId, Instant from, Instant to);
}
