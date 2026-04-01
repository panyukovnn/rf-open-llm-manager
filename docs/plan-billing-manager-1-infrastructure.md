# 1. Инфраструктура и база данных

## Структура пакетов

```
ru.panyukovnn.llmrfrouterbillingmanager
├── config/          — SecurityConfig, WebConfig, YookassaConfig
├── controller/      — REST-контроллеры
├── client/          — HTTP-клиенты (LiteLLM, YooKassa)
├── dto/             — DTO для API и интеграций
├── exception/       — Кастомные исключения
├── mapper/          — MapStruct-мапперы
├── model/           — JPA-сущности и enum-ы
├── property/        — @ConfigurationProperties классы
├── repository/      — Spring Data JPA репозитории
├── scheduler/       — Планировщики (сброс лимитов, проверка подписок)
├── service/
│   └── impl/        — Реализации сервисов
└── util/            — Утилитарные классы
```

## Зависимости (build.gradle)

- [ ] Добавить `spring-boot-starter-data-jpa`
- [ ] Добавить `spring-boot-starter-security`
- [ ] Добавить `spring-boot-starter-oauth2-client`
- [ ] Добавить `spring-boot-starter-oauth2-resource-server` (JWT для API-запросов от фронта)
- [ ] Добавить драйвер `org.postgresql:postgresql`
- [ ] Добавить `org.liquibase:liquibase-core`
- [ ] Добавить `org.mapstruct:mapstruct` + `mapstruct-processor`
- [ ] Исправить `bootJar.archiveFileName` на `billing-manager.jar`

## Конфигурация (application.yml)

- [ ] Настроить datasource (PostgreSQL, URL/credentials через env)
- [ ] Настроить Liquibase (changelog path)
- [ ] Настроить Spring Security OAuth2 (Google client-id/secret через env)
- [ ] Секция `app.litellm` — base-url, master-key
- [ ] Секция `app.yookassa` — shop-id, secret-key, return-url
- [ ] Секция `app.subscription` — grace-period-days

## Liquibase-миграции

- [ ] `001_create_app_user.sql` — таблица `app_user` (id UUID PK, email, name, google_id, created_at, updated_at)
- [ ] `002_create_subscription_plan.sql` — таблица `subscription_plan` (id UUID PK, name, description, monthly_token_limit, price_kopecks, active, created_at)
- [ ] `003_create_user_subscription.sql` — таблица `user_subscription` (id UUID PK, app_user_id FK, subscription_plan_id FK, status, tokens_used, period_start, period_end, created_at)
- [ ] `004_create_api_key.sql` — таблица `api_key` (id UUID PK, app_user_id FK, key_hash, litellm_key_id, name, active, created_at, revoked_at)
- [ ] `005_create_usage_event.sql` — таблица `usage_event` (id UUID PK, api_key_id FK, app_user_id FK, model, prompt_tokens, completion_tokens, total_tokens, cost_usd, litellm_call_id, created_at)
- [ ] `006_create_payment.sql` — таблица `payment` (id UUID PK, app_user_id FK, subscription_plan_id FK, yookassa_payment_id, amount_kopecks, status, created_at, updated_at)
- [ ] `007_insert_default_plans.sql` — начальные тарифные планы (Free / Standard / Pro)
- [ ] Создать `db/changelog/db.changelog-master.yaml` с include всех миграций

## @ConfigurationProperties классы

- [ ] `LitellmProperty` — baseUrl, masterKey
- [ ] `YookassaProperty` — shopId, secretKey, returnUrl
- [ ] `SubscriptionProperty` — gracePeriodDays

## Тесты

- [ ] `when_applicationContext_then_loadsSuccessfully` — smoke-тест контекста (с тестовым application-test.yml + H2)