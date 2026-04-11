package ru.panyukovnn.rfopenllmbillingmanager.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.property.BillingManagerProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.AppUserRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AppUserRepository appUserRepository;
    private final BillingManagerProperty billingManagerProperty;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        AppUser appUser = appUserRepository.findByGoogleId(googleId)
                .orElseGet(() -> createUser(googleId, email, name));

        log.info("OAuth2 авторизация: userId={}, email={}", appUser.getId(), appUser.getEmail());

        String token = jwtTokenProvider.generateToken(appUser.getId(), appUser.getEmail());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(billingManagerProperty.getFrontendRedirectUrl())
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private AppUser createUser(String googleId, String email, String name) {
        AppUser appUser = AppUser.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .build();

        return appUserRepository.save(appUser);
    }
}
