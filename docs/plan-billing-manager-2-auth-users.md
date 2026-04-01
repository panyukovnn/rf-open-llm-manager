# 2. Аутентификация и управление пользователями

## Модель

- [ ] Сущность `AppUser` (id, email, name, googleId, createdAt, updatedAt)

## Security

- [ ] `SecurityConfig` — OAuth2 login через Google, JWT-сессия (stateless для API)
- [ ] `OAuth2UserProcessingService` (implements OAuth2UserService) — при первом входе создаёт AppUser, при повторном — возвращает существующего
- [ ] JWT-генерация после успешного OAuth2 — `JwtTokenProvider` (генерация/валидация токена)
- [ ] `JwtAuthenticationFilter` — фильтр для проверки JWT в заголовке Authorization
- [ ] Redirect на фронтенд с JWT-токеном после успешного OAuth2 логина (через redirect URL + query param)

## Репозиторий

- [ ] `AppUserRepository` — `findByGoogleId(String googleId)`, `findByEmail(String email)`

## Сервис

- [ ] `AppUserService` (интерфейс) — `findCurrentUser()`, `findByGoogleId(String)`
- [ ] `AppUserServiceImpl` — реализация

## REST API

- [ ] `GET /api/v1/users/me` — получить профиль текущего пользователя
- [ ] DTO: `UserProfileResponse` (id, email, name, currentPlan, tokensUsed, tokenLimit)

## Маппер

- [ ] `AppUserMapper` — entity <-> DTO

## Тесты

- [ ] `when_findCurrentUser_then_success`
- [ ] `when_findCurrentUser_withInvalidToken_then_unauthorizedException`
- [ ] `when_oauthLogin_withNewUser_then_userCreated`
- [ ] `when_oauthLogin_withExistingUser_then_existingUserReturned`