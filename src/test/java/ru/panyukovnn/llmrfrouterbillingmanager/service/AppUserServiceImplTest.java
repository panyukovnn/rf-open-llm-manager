package ru.panyukovnn.llmrfrouterbillingmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.impl.AppUserServiceImpl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void when_findCurrentUser_then_success() {
        UUID userId = UUID.randomUUID();
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

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getEmail(), result.getEmail());
    }

    @Test
    void when_findCurrentUser_withNonExistentUser_then_throwsException() {
        UUID userId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userId.toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, appUserService::findCurrentUser);
    }
}
