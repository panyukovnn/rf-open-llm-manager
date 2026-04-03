package ru.panyukovnn.llmrfrouterbillingmanager.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2UserProcessingServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Spy
    @InjectMocks
    private OAuth2UserProcessingService oAuth2UserProcessingService;

    @Test
    void when_oauthLogin_withNewUser_then_userCreated() {
        Map<String, Object> attributes = Map.of(
                "sub", "google-new-123",
                "email", "new@example.com",
                "name", "New User"
        );
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.emptyList(), attributes, "sub");

        OAuth2UserRequest userRequest = buildUserRequest();
        doReturn(oAuth2User).when(oAuth2UserProcessingService).callParentLoadUser(userRequest);
        when(appUserRepository.findByGoogleId("google-new-123")).thenReturn(Optional.empty());
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        oAuth2UserProcessingService.loadUser(userRequest);

        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void when_oauthLogin_withExistingUser_then_existingUserReturned() {
        Map<String, Object> attributes = Map.of(
                "sub", "google-existing-123",
                "email", "existing@example.com",
                "name", "Existing User"
        );
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.emptyList(), attributes, "sub");

        AppUser existingUser = AppUser.builder()
                .id(UUID.randomUUID())
                .googleId("google-existing-123")
                .email("existing@example.com")
                .name("Existing User")
                .build();

        OAuth2UserRequest userRequest = buildUserRequest();
        doReturn(oAuth2User).when(oAuth2UserProcessingService).callParentLoadUser(userRequest);
        when(appUserRepository.findByGoogleId("google-existing-123"))
                .thenReturn(Optional.of(existingUser));

        oAuth2UserProcessingService.loadUser(userRequest);

        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    private OAuth2UserRequest buildUserRequest() {
        ClientRegistration clientRegistration = ClientRegistration
                .withRegistrationId("google")
                .clientId("test-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/callback")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600));

        return new OAuth2UserRequest(clientRegistration, accessToken);
    }
}
