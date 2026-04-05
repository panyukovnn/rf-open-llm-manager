package ru.panyukovnn.rfopenllmbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UserProfileResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    UserProfileResponse toUserProfileResponse(AppUser appUser);
}
