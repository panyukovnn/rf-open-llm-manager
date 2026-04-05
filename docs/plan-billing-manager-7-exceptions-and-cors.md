# 7. Обработка ошибок и CORS

## Исключения

- [x] `NoActiveSubscriptionException` — нет активной подписки
- [x] `TokenLimitExceededException` — лимит токенов исчерпан
- [x] ~~`SubscriptionPlanNotFoundException`~~ — используется обобщённый `BusinessException` стартера с кодом
- [x] ~~`ApiKeyNotFoundException`~~ — используется обобщённый `BusinessException` стартера с кодом
- [x] ~~`PaymentNotFoundException`~~ — используется обобщённый `BusinessException` стартера с кодом
- [x] ~~`AccessDeniedException`~~ — используется обобщённый `BusinessException` стартера с кодом
- [x] ~~`LitellmIntegrationException`~~ — используется `CriticalException` стартера с кодом
- [x] ~~`YookassaIntegrationException`~~ — используется `CriticalException` стартера с кодом

## Глобальный обработчик

- [x] ~~`GlobalExceptionHandler`~~ — предоставляется стартером `reference-model-starter`: `CommonControllerExceptionHandler` (@RestControllerAdvice)
- [x] ~~DTO: `ErrorResponse`~~ — предоставляется стартером: `CommonResponse<Void>` с полями code/message/timestamp

## CORS

- [x] `WebConfig` — настройка CORS для фронтенд-домена (через application.yml параметр `billing-manager.cors.allowed-origins`)

## Тесты

- [x] ~~`when_noActiveSubscription_then_httpForbidden`~~ — неактуально: стартер маппит `BusinessException` в HTTP 400, а не 403
- [x] ~~`when_planNotFound_then_httpNotFound`~~ — неактуально: отдельный `SubscriptionPlanNotFoundException` не создавался
- [x] `when_corsPreflight_withAllowedOrigin_then_allowed`
- [x] `when_corsPreflight_withDisallowedOrigin_then_forbidden`