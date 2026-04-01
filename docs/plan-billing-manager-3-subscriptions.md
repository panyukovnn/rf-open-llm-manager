# 3. Подписки и тарифные планы

## Модели

- [ ] Сущность `SubscriptionPlan` (id, name, description, monthlyTokenLimit, priceKopecks, active, createdAt)
- [ ] Сущность `UserSubscription` (id, appUser, subscriptionPlan, status, tokensUsed, periodStart, periodEnd, createdAt)
- [ ] Enum `SubscriptionStatus` — ACTIVE, EXPIRED, CANCELLED

## Репозитории

- [ ] `SubscriptionPlanRepository` — `findAllByActiveTrue()`
- [ ] `UserSubscriptionRepository` — `findByAppUserIdAndStatus(UUID, SubscriptionStatus)`, `findAllByStatusAndPeriodEndBefore(SubscriptionStatus, LocalDateTime)`

## Сервисы

- [ ] `SubscriptionPlanService` (интерфейс) — `findAllActivePlans()`, `findById(UUID)`
- [ ] `SubscriptionPlanServiceImpl`
- [ ] `UserSubscriptionService` (интерфейс) — `findActiveSubscription(UUID userId)`, `activateSubscription(UUID userId, UUID planId)`, `deductTokens(UUID userId, long tokens)`, `hasAvailableTokens(UUID userId)`
- [ ] `UserSubscriptionServiceImpl`

## REST API

- [ ] `GET /api/v1/subscription-plans` — список доступных тарифов
- [ ] `GET /api/v1/subscriptions/current` — текущая подписка пользователя
- [ ] `POST /api/v1/subscriptions` — оформить подписку (инициирует оплату через ЮKassa)
- [ ] DTO: `SubscriptionPlanResponse`, `UserSubscriptionResponse`, `CreateSubscriptionRequest`

## Планировщик

- [ ] `SubscriptionExpirationScheduler` — ежедневно проверяет истёкшие подписки, меняет статус на EXPIRED, деактивирует API-ключи в LiteLLM

## Маппер

- [ ] `SubscriptionMapper` — entity <-> DTO

## Тесты

- [ ] `when_findAllActivePlans_then_success`
- [ ] `when_findActiveSubscription_then_success`
- [ ] `when_findActiveSubscription_withNoSubscription_then_emptyResult`
- [ ] `when_activateSubscription_then_subscriptionCreated`
- [ ] `when_deductTokens_then_tokensUsedIncremented`
- [ ] `when_deductTokens_withExceededLimit_then_tokenLimitExceededException`
- [ ] `when_expirationScheduler_withExpiredSubscription_then_statusChangedToExpired`