package ru.panyukovnn.rfopenllmbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    SessionResponse toSessionResponse(Session session);

    SessionListItemResponse toSessionListItemResponse(Session session);
}
