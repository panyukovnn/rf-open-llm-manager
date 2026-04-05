package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    List<MessageResponse> findBySession(UUID userId, UUID sessionId, int page, int size);
}