package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    @Override
    public AppUser findCurrentUser() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return appUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь не найден по идентификатору: " + userId));
    }

    @Override
    public Optional<AppUser> findByGoogleId(String googleId) {
        return appUserRepository.findByGoogleId(googleId);
    }
}
