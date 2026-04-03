package ru.panyukovnn.llmrfrouterbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.AppUserMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Override
    public AppUser findCurrentUser() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return appUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(
                        "b4e2",
                        "Пользователь не найден"));
    }

    @Override
    public UserProfileResponse findCurrentUserProfile() {
        AppUser currentUser = findCurrentUser();

        return appUserMapper.toUserProfileResponse(currentUser);
    }

    @Override
    public Optional<AppUser> findByGoogleId(String googleId) {
        return appUserRepository.findByGoogleId(googleId);
    }
}
