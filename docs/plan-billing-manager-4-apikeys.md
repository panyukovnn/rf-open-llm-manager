# 4. Управление API-ключами и интеграция с LiteLLM

## Модель

- [x] Сущность `ApiKey` (id, appUser, keyHash, litellmKeyId, name, active, createdAt, revokedAt)

## HTTP-клиент LiteLLM

- [x] `LitellmClient` (интерфейс) — `generateKey(LitellmKeyGenerateRequest)`, `revokeKey(String litellmKeyId)`, `updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest)`
- [x] `LitellmClientImpl` — реализация через RestClient
- [x] DTO: `LitellmKeyGenerateRequest` (models, duration, maxBudget, metadata, userId)
- [x] DTO: `LitellmKeyGenerateResponse` (key, keyId, expiresAt)
- [x] DTO: `LitellmKeyUpdateRequest` (maxBudget, tpmLimit)

## Репозиторий

- [x] `ApiKeyRepository` — `findAllByAppUserIdAndActiveTrue(UUID)`, `findByKeyHash(String)`, `findByLitellmKeyId(String)`

## Сервис

- [x] `ApiKeyService` (интерфейс) — `generateKey(UUID userId, String keyName)`, `revokeKey(UUID userId, UUID keyId)`, `findUserKeys(UUID userId)`, `deactivateAllUserKeys(UUID userId)`
- [x] `ApiKeyServiceImpl` — при генерации вызывает LiteLLM `/key/generate`, сохраняет хеш ключа и litellm_key_id; при отзыве — вызывает LiteLLM `/key/delete`

## REST API

- [x] `POST /api/v1/api-keys` — сгенерировать новый ключ (возвращает ключ один раз)
- [x] `GET /api/v1/api-keys` — список ключей пользователя (без самого ключа, только метаданные)
- [x] `DELETE /api/v1/api-keys/{keyId}` — отозвать ключ
- [x] DTO: `CreateApiKeyRequest` (name), `ApiKeyResponse` (id, name, keyPrefix, active, createdAt), `CreateApiKeyResponse` (id, name, key — полный ключ, показывается один раз)

## Тесты

- [x] `when_generateKey_then_keyCreatedInLitellmAndSaved`
- [x] `when_generateKey_withNoActiveSubscription_then_noActiveSubscriptionException`
- [x] `when_revokeKey_then_keyDeactivatedInLitellmAndDb`
- [x] `when_revokeKey_withOtherUsersKey_then_accessDeniedException`
- [x] `when_findUserKeys_then_success`
- [x] `when_deactivateAllUserKeys_then_allKeysRevoked`