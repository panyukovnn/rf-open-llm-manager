# 6. Интеграция с ЮKassa

## Модель

- [x] Сущность `Payment` (id, appUser, subscriptionPlan, yookassaPaymentId, amountKopecks, status, createdAt, updatedAt)
- [x] Enum `PaymentStatus` — PENDING, SUCCEEDED, CANCELLED

## HTTP-клиент ЮKassa

- [x] `YookassaClient` (интерфейс) — `createPayment(YookassaCreatePaymentRequest)`, `getPaymentInfo(String yookassaPaymentId)`
- [x] `YookassaClientImpl` — реализация через RestClient (Basic Auth: shopId:secretKey)
- [x] DTO: `YookassaCreatePaymentRequest` (amountValue, amountCurrency, description, returnUrl, metadata)
- [x] DTO: `YookassaCreatePaymentResponse` (id, status, confirmationUrl)
- [x] DTO: `YookassaWebhookPayload` (event, object — с полями id, status, metadata)

## Репозиторий

- [x] `PaymentRepository` — `findByYookassaPaymentId(String)`, `findAllByAppUserId(UUID)`

## Сервис

- [x] `PaymentService` (интерфейс) — `initiatePayment(UUID userId, UUID planId)`, `processWebhook(YookassaWebhookPayload)`, `findUserPayments(UUID userId)`
- [x] `PaymentServiceImpl`:
  - `initiatePayment` — создаёт Payment (PENDING), вызывает ЮKassa API, возвращает confirmation URL
  - `processWebhook` — при статусе `payment.succeeded` активирует подписку через `UserSubscriptionService.activateSubscription()`; при `payment.canceled` — обновляет статус Payment

## Webhook-контроллер

- [x] `POST /api/v1/internal/yookassa-webhook` — эндпоинт для callback от ЮKassa
- [x] Проверка IP-адреса ЮKassa (или подпись) для безопасности
- [x] Эндпоинт исключён из OAuth2

## REST API (для фронта)

- [x] `POST /api/v1/payments` — инициировать оплату подписки, возвращает URL для оплаты
- [x] `GET /api/v1/payments` — история платежей
- [x] DTO: `InitiatePaymentRequest` (subscriptionPlanId)
- [x] DTO: `InitiatePaymentResponse` (paymentId, confirmationUrl)
- [x] DTO: `PaymentResponse` (id, planName, amountKopecks, status, createdAt)

## Тесты

- [x] `when_initiatePayment_then_paymentCreatedAndConfirmationUrlReturned`
- [x] `when_initiatePayment_withInactivePlan_then_subscriptionPlanNotFoundException`
- [x] `when_processWebhook_withSucceededStatus_then_subscriptionActivated`
- [x] `when_processWebhook_withCancelledStatus_then_paymentStatusUpdated`
- [x] `when_processWebhook_withUnknownPaymentId_then_paymentNotFoundException`
- [x] `when_findUserPayments_then_success`