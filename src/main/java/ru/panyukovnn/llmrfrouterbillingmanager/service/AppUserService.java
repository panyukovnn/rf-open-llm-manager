package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;

import java.util.Optional;

public interface AppUserService {

    AppUser findCurrentUser();

    UserProfileResponse findCurrentUserProfile();

    Optional<AppUser> findByGoogleId(String googleId);
}
