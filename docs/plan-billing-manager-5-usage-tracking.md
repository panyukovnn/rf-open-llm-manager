# 5. Учёт использования токенов (LiteLLM callback)

## Модель

- [x] Сущность `UsageEvent` (id, apiKey, appUser, model, promptTokens, completionTokens, totalTokens, costUsd, litellmCallId, createdAt)

## Репозиторий

- [x] `UsageEventRepository` — `findAllByAppUserIdAndCreatedAtBetween(UUID, LocalDateTime, LocalDateTime)`

## Callback-контроллер

- [x] `POST /api/v1/internal/usage-callback` — эндпоинт для LiteLLM generic_api callback
- [x] Авторизация по секретному заголовку (callback-secret), эндпоинт исключён из OAuth2
- [x] DTO: `LitellmUsageCallbackPayload` — поля: id, call_type, user_api_key_hash, model, prompt_tokens, completion_tokens, total_tokens, response_cost, status, startTime, endTime
- [x] Поддержка batch-формата (JSON-массив payload-ов)

## Сервис

- [x] `UsageTrackingService` (интерфейс) — `processUsageCallback(List<LitellmUsageCallbackPayload>)`, `findUsageHistory(UUID userId, LocalDateTime from, LocalDateTime to)`
- [x] `UsageTrackingServiceImpl` — для каждого payload: найти ApiKey по keyHash, создать UsageEvent, вызвать `UserSubscriptionService.deductTokens()`

## REST API (для фронта)

- [x] `GET /api/v1/usage?from={from}&to={to}` — история использования текущего пользователя
- [x] DTO: `UsageEventResponse` (model, promptTokens, completionTokens, totalTokens, createdAt)
- [x] DTO: `UsageSummaryResponse` (totalTokensUsed, tokenLimit, usageByModel — Map<String, Long>)

## Тесты

- [x] `when_processUsageCallback_then_usageEventSavedAndTokensDeducted`
- [x] `when_processUsageCallback_withBatch_then_allEventsProcessed`
- [x] `when_processUsageCallback_withUnknownKeyHash_then_eventSkippedAndLogged`
- [x] `when_processUsageCallback_withInvalidSecret_then_forbidden`
- [x] `when_findUsageHistory_then_success`
