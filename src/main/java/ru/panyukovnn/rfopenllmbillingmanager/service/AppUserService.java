package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;

import java.util.Optional;

public interface AppUserService {

    AppUser findCurrentUser();

    UserProfileResponse findCurrentUserProfile();

    Optional<AppUser> findByGoogleId(String googleId);
}
