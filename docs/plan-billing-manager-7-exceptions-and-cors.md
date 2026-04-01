# 7. Обработка ошибок и CORS

## Исключения

- [ ] `NoActiveSubscriptionException` — нет активной подписки
- [ ] `TokenLimitExceededException` — лимит токенов исчерпан
- [ ] `SubscriptionPlanNotFoundException` — тарифный план не найден
- [ ] `ApiKeyNotFoundException` — API-ключ не найден
- [ ] `PaymentNotFoundException` — платёж не найден
- [ ] `AccessDeniedException` — доступ запрещён (чужой ресурс)
- [ ] `LitellmIntegrationException` — ошибка при обращении к LiteLLM
- [ ] `YookassaIntegrationException` — ошибка при обращении к ЮKassa

## Глобальный обработчик

- [ ] `GlobalExceptionHandler` (@RestControllerAdvice) — маппинг исключений на HTTP-статусы
- [ ] DTO: `ErrorResponse` (code, message, timestamp)

## CORS

- [ ] `WebConfig` — настройка CORS для фронтенд-домена (через application.yml параметр `app.cors.allowed-origins`)

## Тесты

- [ ] `when_noActiveSubscription_then_httpForbidden`
- [ ] `when_planNotFound_then_httpNotFound`