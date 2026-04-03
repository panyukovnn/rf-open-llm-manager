package ru.panyukovnn.llmrfrouterbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.AppUserMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.impl.AppUserServiceImpl;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplUnitTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AppUserMapper appUserMapper;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Nested
    class FindCurrentUser {

        @Test
        void when_findCurrentUser_then_success() {
            UUID userId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
            AppUser expectedUser = AppUser.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .googleId("google-123")
                    .build();

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn(userId.toString());
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(appUserRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

            AppUser result = appUserService.findCurrentUser();

            assertEquals("a1b2c3d4-e5f6-7890-abcd-ef1234567890", result.getId().toString());
            assertEquals("test@example.com", result.getEmail());
            assertEquals("Test User", result.getName());
            assertEquals("google-123", result.getGoogleId());
            verify(appUserRepository).findById(userId);
        }

        @Test
        void when_findCurrentUser_withNonExistentUser_then_throwsException() {
            UUID userId = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn(userId.toString());
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, appUserService::findCurrentUser);
        }
    }

    @Nested
    class FindCurrentUserProfile {

        @Test
        void when_findCurrentUserProfile_then_success() {
            UUID userId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
            AppUser user = AppUser.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .build();
            UserProfileResponse expectedResponse = UserProfileResponse.builder()
                    .id(userId)
                    .email("test@example.com")
                    .name("Test User")
                    .build();

            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn(userId.toString());
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
            when(appUserMapper.toUserProfileResponse(user)).thenReturn(expectedResponse);

            UserProfileResponse result = appUserService.findCurrentUserProfile();

            assertEquals(userId, result.getId());
            assertEquals("test@example.com", result.getEmail());
            assertEquals("Test User", result.getName());
            verify(appUserMapper).toUserProfileResponse(user);
        }
    }
}
