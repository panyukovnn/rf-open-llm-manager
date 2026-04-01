# 5. Учёт использования токенов (LiteLLM callback)

## Модель

- [ ] Сущность `UsageEvent` (id, apiKey, appUser, model, promptTokens, completionTokens, totalTokens, costUsd, litellmCallId, createdAt)

## Репозиторий

- [ ] `UsageEventRepository` — `findAllByAppUserIdAndCreatedAtBetween(UUID, LocalDateTime, LocalDateTime)`

## Callback-контроллер

- [ ] `POST /api/v1/internal/usage-callback` — эндпоинт для LiteLLM generic_api callback
- [ ] Авторизация по секретному заголовку (callback-secret), эндпоинт исключён из OAuth2
- [ ] DTO: `LitellmUsageCallbackPayload` — поля: id, call_type, user_api_key_hash, model, prompt_tokens, completion_tokens, total_tokens, response_cost, status, startTime, endTime
- [ ] Поддержка batch-формата (JSON-массив payload-ов)

## Сервис

- [ ] `UsageTrackingService` (интерфейс) — `processUsageCallback(List<LitellmUsageCallbackPayload>)`, `findUsageHistory(UUID userId, LocalDateTime from, LocalDateTime to)`
- [ ] `UsageTrackingServiceImpl` — для каждого payload: найти ApiKey по keyHash, создать UsageEvent, вызвать `UserSubscriptionService.deductTokens()`

## REST API (для фронта)

- [ ] `GET /api/v1/usage?from={from}&to={to}` — история использования текущего пользователя
- [ ] DTO: `UsageEventResponse` (model, promptTokens, completionTokens, totalTokens, createdAt)
- [ ] DTO: `UsageSummaryResponse` (totalTokensUsed, tokenLimit, usageByModel — Map<String, Long>)

## Тесты

- [ ] `when_processUsageCallback_then_usageEventSavedAndTokensDeducted`
- [ ] `when_processUsageCallback_withBatch_then_allEventsProcessed`
- [ ] `when_processUsageCallback_withUnknownKeyHash_then_eventSkippedAndLogged`
- [ ] `when_processUsageCallback_withInvalidSecret_then_forbidden`
- [ ] `when_findUsageHistory_then_success`