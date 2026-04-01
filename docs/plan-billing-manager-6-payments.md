# 6. Интеграция с ЮKassa

## Модель

- [ ] Сущность `Payment` (id, appUser, subscriptionPlan, yookassaPaymentId, amountKopecks, status, createdAt, updatedAt)
- [ ] Enum `PaymentStatus` — PENDING, SUCCEEDED, CANCELLED

## HTTP-клиент ЮKassa

- [ ] `YookassaClient` (интерфейс) — `createPayment(YookassaCreatePaymentRequest)`, `getPaymentInfo(String yookassaPaymentId)`
- [ ] `YookassaClientImpl` — реализация через RestClient (Basic Auth: shopId:secretKey)
- [ ] DTO: `YookassaCreatePaymentRequest` (amountValue, amountCurrency, description, returnUrl, metadata)
- [ ] DTO: `YookassaCreatePaymentResponse` (id, status, confirmationUrl)
- [ ] DTO: `YookassaWebhookPayload` (event, object — с полями id, status, metadata)

## Репозиторий

- [ ] `PaymentRepository` — `findByYookassaPaymentId(String)`, `findAllByAppUserId(UUID)`

## Сервис

- [ ] `PaymentService` (интерфейс) — `initiatePayment(UUID userId, UUID planId)`, `processWebhook(YookassaWebhookPayload)`, `findUserPayments(UUID userId)`
- [ ] `PaymentServiceImpl`:
  - `initiatePayment` — создаёт Payment (PENDING), вызывает ЮKassa API, возвращает confirmation URL
  - `processWebhook` — при статусе `payment.succeeded` активирует подписку через `UserSubscriptionService.activateSubscription()`; при `payment.canceled` — обновляет статус Payment

## Webhook-контроллер

- [ ] `POST /api/v1/internal/yookassa-webhook` — эндпоинт для callback от ЮKassa
- [ ] Проверка IP-адреса ЮKassa (или подпись) для безопасности
- [ ] Эндпоинт исключён из OAuth2

## REST API (для фронта)

- [ ] `POST /api/v1/payments` — инициировать оплату подписки, возвращает URL для оплаты
- [ ] `GET /api/v1/payments` — история платежей
- [ ] DTO: `InitiatePaymentRequest` (subscriptionPlanId)
- [ ] DTO: `InitiatePaymentResponse` (paymentId, confirmationUrl)
- [ ] DTO: `PaymentResponse` (id, planName, amountKopecks, status, createdAt)

## Тесты

- [ ] `when_initiatePayment_then_paymentCreatedAndConfirmationUrlReturned`
- [ ] `when_initiatePayment_withInactivePlan_then_subscriptionPlanNotFoundException`
- [ ] `when_processWebhook_withSucceededStatus_then_subscriptionActivated`
- [ ] `when_processWebhook_withCancelledStatus_then_paymentStatusUpdated`
- [ ] `when_processWebhook_withUnknownPaymentId_then_paymentNotFoundException`
- [ ] `when_findUserPayments_then_success`