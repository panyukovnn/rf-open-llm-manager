package ru.panyukovnn.llmrfrouterbillingmanager.service;

import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;

import java.util.Optional;

public interface AppUserService {

    AppUser findCurrentUser();

    Optional<AppUser> findByGoogleId(String googleId);
}
