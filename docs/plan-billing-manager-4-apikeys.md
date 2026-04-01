# 4. Управление API-ключами и интеграция с LiteLLM

## Модель

- [ ] Сущность `ApiKey` (id, appUser, keyHash, litellmKeyId, name, active, createdAt, revokedAt)

## HTTP-клиент LiteLLM

- [ ] `LitellmClient` (интерфейс) — `generateKey(LitellmKeyGenerateRequest)`, `revokeKey(String litellmKeyId)`, `updateKeyBudget(String litellmKeyId, LitellmKeyUpdateRequest)`
- [ ] `LitellmClientImpl` — реализация через RestClient
- [ ] DTO: `LitellmKeyGenerateRequest` (models, duration, maxBudget, metadata, userId)
- [ ] DTO: `LitellmKeyGenerateResponse` (key, keyId, expiresAt)
- [ ] DTO: `LitellmKeyUpdateRequest` (maxBudget, tpmLimit)

## Репозиторий

- [ ] `ApiKeyRepository` — `findAllByAppUserIdAndActiveTrue(UUID)`, `findByKeyHash(String)`, `findByLitellmKeyId(String)`

## Сервис

- [ ] `ApiKeyService` (интерфейс) — `generateKey(UUID userId, String keyName)`, `revokeKey(UUID userId, UUID keyId)`, `findUserKeys(UUID userId)`, `deactivateAllUserKeys(UUID userId)`
- [ ] `ApiKeyServiceImpl` — при генерации вызывает LiteLLM `/key/generate`, сохраняет хеш ключа и litellm_key_id; при отзыве — вызывает LiteLLM `/key/delete`

## REST API

- [ ] `POST /api/v1/api-keys` — сгенерировать новый ключ (возвращает ключ один раз)
- [ ] `GET /api/v1/api-keys` — список ключей пользователя (без самого ключа, только метаданные)
- [ ] `DELETE /api/v1/api-keys/{keyId}` — отозвать ключ
- [ ] DTO: `CreateApiKeyRequest` (name), `ApiKeyResponse` (id, name, keyPrefix, active, createdAt), `CreateApiKeyResponse` (id, name, key — полный ключ, показывается один раз)

## Тесты

- [ ] `when_generateKey_then_keyCreatedInLitellmAndSaved`
- [ ] `when_generateKey_withNoActiveSubscription_then_noActiveSubscriptionException`
- [ ] `when_revokeKey_then_keyDeactivatedInLitellmAndDb`
- [ ] `when_revokeKey_withOtherUsersKey_then_accessDeniedException`
- [ ] `when_findUserKeys_then_success`
- [ ] `when_deactivateAllUserKeys_then_allKeysRevoked`