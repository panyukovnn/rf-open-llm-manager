package ru.panyukovnn.llmrfrouterbillingmanager.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.property.BillingManagerProperty;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;

import java.io.IOException;
import java.util.NoSuchElementException;

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

        AppUser appUser = appUserRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь не найден по Google ID: " + googleId));

        String token = jwtTokenProvider.generateToken(appUser.getId(), appUser.getEmail());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(billingManagerProperty.getFrontendRedirectUrl())
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
