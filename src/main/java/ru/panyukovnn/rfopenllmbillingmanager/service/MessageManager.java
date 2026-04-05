package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageManager {

    List<MessageResponse> handleFindSessionMessages(UUID sessionId, int page, int size);
}