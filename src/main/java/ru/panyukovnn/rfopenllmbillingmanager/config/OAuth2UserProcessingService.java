package ru.panyukovnn.rfopenllmbillingmanager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.repository.AppUserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserProcessingService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;
    private final AppUserRepository appUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        appUserRepository.findByGoogleId(googleId)
                .orElseGet(() -> createAppUser(googleId, email, name));

        return oAuth2User;
    }

    private AppUser createAppUser(String googleId, String email, String name) {
        AppUser appUser = AppUser.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .build();

        return appUserRepository.save(appUser);
    }
}
