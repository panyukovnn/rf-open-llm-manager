package ru.panyukovnn.llmrfrouterbillingmanager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserProcessingService extends DefaultOAuth2UserService {

    private final AppUserRepository appUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = callParentLoadUser(userRequest);
        processOAuth2User(oAuth2User);

        return oAuth2User;
    }

    /**
     * Делегирование вызова родительского метода для возможности тестирования
     */
    OAuth2User callParentLoadUser(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }

    private void processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        appUserRepository.findByGoogleId(googleId)
                .orElseGet(() -> createAppUser(googleId, email, name));
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
