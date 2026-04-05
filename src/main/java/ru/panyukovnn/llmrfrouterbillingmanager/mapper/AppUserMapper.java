package ru.panyukovnn.llmrfrouterbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    UserProfileResponse toUserProfileResponse(AppUser appUser);
}
