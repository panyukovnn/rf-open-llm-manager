package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.AppUserMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;
import ru.panyukovnn.rfopenllmbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
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
