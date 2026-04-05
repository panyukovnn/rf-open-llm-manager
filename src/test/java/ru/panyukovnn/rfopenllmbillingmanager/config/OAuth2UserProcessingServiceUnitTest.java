package ru.panyukovnn.rfopenllmbillingmanager.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.repository.AppUserRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2UserProcessingServiceUnitTest {

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;
    @Mock
    private AppUserRepository appUserRepository;

    @Nested
    class LoadUser {

        @Test
        void when_loadUser_withNewUser_then_userCreated() {
            OAuth2UserProcessingService service = new OAuth2UserProcessingService(delegate, appUserRepository);

            Map<String, Object> attributes = Map.of(
                    "sub", "google-new-123",
                    "email", "new@example.com",
                    "name", "New User"
            );
            OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");

            OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
            when(delegate.loadUser(userRequest)).thenReturn(oAuth2User);
            when(appUserRepository.findByGoogleId("google-new-123")).thenReturn(Optional.empty());
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

            service.loadUser(userRequest);

            verify(appUserRepository).save(any(AppUser.class));
        }

        @Test
        void when_loadUser_withExistingUser_then_existingUserReturned() {
            OAuth2UserProcessingService service = new OAuth2UserProcessingService(delegate, appUserRepository);

            Map<String, Object> attributes = Map.of(
                    "sub", "google-existing-123",
                    "email", "existing@example.com",
                    "name", "Existing User"
            );
            OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "sub");

            AppUser existingUser = AppUser.builder()
                    .id(UUID.randomUUID())
                    .googleId("google-existing-123")
                    .email("existing@example.com")
                    .name("Existing User")
                    .build();

            OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
            when(delegate.loadUser(userRequest)).thenReturn(oAuth2User);
            when(appUserRepository.findByGoogleId("google-existing-123"))
                    .thenReturn(Optional.of(existingUser));

            service.loadUser(userRequest);

            verify(appUserRepository, never()).save(any(AppUser.class));
        }
    }
}
