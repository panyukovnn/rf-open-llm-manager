package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UsageEventResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.ApiKey;
import ru.panyukovnn.rfopenllmbillingmanager.model.UsageEvent;
import ru.panyukovnn.rfopenllmbillingmanager.repository.ApiKeyRepository;
import ru.panyukovnn.rfopenllmbillingmanager.repository.UsageEventRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.UsageTrackingService;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageTrackingServiceImpl implements UsageTrackingService {

    private final UsageEventRepository usageEventRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final UserSubscriptionService userSubscriptionService;

    @Override
    @Transactional
    public void processUsageCallback(List<LitellmUsageCallbackPayload> payloads) {
        for (LitellmUsageCallbackPayload payload : payloads) {
            processSinglePayload(payload);
        }
    }

    @Override
    public List<UsageEventResponse> findUsageHistory(UUID userId, Instant from, Instant to) {
        List<UsageEvent> events = usageEventRepository
                .findAllByAppUserIdAndCreateTimeBetween(userId, from, to);

        return events.stream()
                .map(this::toUsageEventResponse)
                .toList();
    }

    /**
     * Обрабатывает один payload: находит API-ключ и сохраняет событие использования
     */
    private void processSinglePayload(LitellmUsageCallbackPayload payload) {
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByKeyHash(payload.getUserApiKeyHash());

        if (apiKeyOpt.isEmpty()) {
            log.warn("Получен callback с неизвестным keyHash {} callId {}",
                    payload.getUserApiKeyHash(), payload.getId());

            return;
        }

        ApiKey apiKey = apiKeyOpt.get();
        usageEventRepository.save(buildUsageEvent(apiKey, payload));

        long tokensToDeduct = Optional.ofNullable(payload.getTotalTokens()).orElse(0L);

        if (tokensToDeduct > 0) {
            userSubscriptionService.deductTokens(apiKey.getAppUserId(), tokensToDeduct);
        }
    }

    private UsageEvent buildUsageEvent(ApiKey apiKey, LitellmUsageCallbackPayload payload) {
        return UsageEvent.builder()
                .apiKeyId(apiKey.getId())
                .appUserId(apiKey.getAppUserId())
                .model(payload.getModel())
                .promptTokens(payload.getPromptTokens())
                .completionTokens(payload.getCompletionTokens())
                .totalTokens(payload.getTotalTokens())
                .costUsd(payload.getResponseCost())
                .litellmCallId(payload.getId())
                .build();
    }

    private UsageEventResponse toUsageEventResponse(UsageEvent event) {
        return UsageEventResponse.builder()
                .model(event.getModel())
                .promptTokens(event.getPromptTokens())
                .completionTokens(event.getCompletionTokens())
                .totalTokens(event.getTotalTokens())
                .createdAt(event.getCreateTime())
                .build();
    }
}
