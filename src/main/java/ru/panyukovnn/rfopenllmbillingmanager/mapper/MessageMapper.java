package ru.panyukovnn.rfopenllmbillingmanager.mapper;

import org.mapstruct.Mapper;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toMessageResponse(Message message);
}