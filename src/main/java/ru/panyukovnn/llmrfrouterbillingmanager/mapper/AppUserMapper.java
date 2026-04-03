package ru.panyukovnn.llmrfrouterbillingmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.llmrfrouterbillingmanager.model.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "currentPlan", ignore = true)
    @Mapping(target = "tokensUsed", ignore = true)
    @Mapping(target = "tokenLimit", ignore = true)
    UserProfileResponse toUserProfileResponse(AppUser appUser);
}
