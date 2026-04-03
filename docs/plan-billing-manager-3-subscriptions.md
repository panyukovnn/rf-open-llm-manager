# 3. Подписки и тарифные планы

## Модели

- [x] Сущность `SubscriptionPlan` (id, name, description, monthlyTokenLimit, priceKopecks, active, createdAt)
- [x] Сущность `UserSubscription` (id, appUser, subscriptionPlan, status, tokensUsed, periodStart, periodEnd, createdAt)
- [x] Enum `SubscriptionStatus` — ACTIVE, EXPIRED, CANCELLED

## Репозитории

- [x] `SubscriptionPlanRepository` — `findAllByActiveTrue()`
- [x] `UserSubscriptionRepository` — `findByAppUserIdAndStatus(UUID, SubscriptionStatus)`, `findAllByStatusAndPeriodEndBefore(SubscriptionStatus, LocalDateTime)`

## Сервисы

- [x] `SubscriptionPlanService` (интерфейс) — `findAllActivePlans()`, `findById(UUID)`
- [x] `SubscriptionPlanServiceImpl`
- [x] `UserSubscriptionService` (интерфейс) — `findActiveSubscription(UUID userId)`, `activateSubscription(UUID userId, UUID planId)`, `deductTokens(UUID userId, long tokens)`, `hasAvailableTokens(UUID userId)`
- [x] `UserSubscriptionServiceImpl`

## REST API

- [x] `GET /api/v1/subscription-plans` — список доступных тарифов
- [x] `GET /api/v1/subscriptions/current` — текущая подписка пользователя
- [x] `POST /api/v1/subscriptions` — оформить подписку (инициирует оплату через ЮKassa)
- [x] DTO: `SubscriptionPlanResponse`, `UserSubscriptionResponse`, `CreateSubscriptionRequest`

## Планировщик

- [x] `SubscriptionExpirationScheduler` — ежедневно проверяет истёкшие подписки, меняет статус на EXPIRED, деактивирует API-ключи в LiteLLM

## Маппер

- [x] `SubscriptionMapper` — entity <-> DTO

## Тесты

- [x] `when_findAllActivePlans_then_success`
- [x] `when_findActiveSubscription_then_success`
- [x] `when_findActiveSubscription_withNoSubscription_then_emptyResult`
- [x] `when_activateSubscription_then_subscriptionCreated`
- [x] `when_deductTokens_then_tokensUsedIncremented`
- [x] `when_deductTokens_withExceededLimit_then_tokenLimitExceededException`
- [x] `when_expirationScheduler_withExpiredSubscription_then_statusChangedToExpired`