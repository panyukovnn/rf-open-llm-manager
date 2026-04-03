# 2. Аутентификация и управление пользователями

## Модель

- [x] Сущность `AppUser` (id, email, name, googleId, createdAt, updatedAt)

## Security

- [x] `SecurityConfig` — OAuth2 login через Google, JWT-сессия (stateless для API)
- [x] `OAuth2UserProcessingService` (implements OAuth2UserService) — при первом входе создаёт AppUser, при повторном — возвращает существующего
- [x] JWT-генерация после успешного OAuth2 — `JwtTokenProvider` (генерация/валидация токена)
- [x] `JwtAuthenticationFilter` — фильтр для проверки JWT в заголовке Authorization
- [x] Redirect на фронтенд с JWT-токеном после успешного OAuth2 логина (через redirect URL + query param)

## Репозиторий

- [x] `AppUserRepository` — `findByGoogleId(String googleId)`, `findByEmail(String email)`

## Сервис

- [x] `AppUserService` (интерфейс) — `findCurrentUser()`, `findByGoogleId(String)`
- [x] `AppUserServiceImpl` — реализация

## REST API

- [x] `GET /api/v1/users/me` — получить профиль текущего пользователя
- [x] DTO: `UserProfileResponse` (id, email, name, currentPlan, tokensUsed, tokenLimit)

## Маппер

- [x] `AppUserMapper` — entity <-> DTO

## Тесты

- [x] `when_findCurrentUser_then_success`
- [x] `when_findCurrentUser_withInvalidToken_then_unauthorizedException`
- [x] `when_oauthLogin_withNewUser_then_userCreated`
- [x] `when_oauthLogin_withExistingUser_then_existingUserReturned`