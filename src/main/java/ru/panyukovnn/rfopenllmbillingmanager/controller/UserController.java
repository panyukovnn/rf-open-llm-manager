package ru.panyukovnn.rfopenllmbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @GetMapping("/me")
    public CommonResponse<UserProfileResponse> findCurrentUserProfile() {
        UserProfileResponse profile = appUserService.findCurrentUserProfile();

        return CommonResponse.<UserProfileResponse>builder()
                .data(profile)
                .build();
    }
}
