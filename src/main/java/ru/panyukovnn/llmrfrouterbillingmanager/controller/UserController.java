package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.mapper.AppUserMapper;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;
import ru.panyukovnn.llmrfrouterbillingmanager.service.AppUserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;

    @GetMapping("/me")
    public UserProfileResponse findCurrentUserProfile() {
        AppUser currentUser = appUserService.findCurrentUser();

        return appUserMapper.toUserProfileResponse(currentUser);
    }
}
