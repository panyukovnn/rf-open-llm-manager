package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    List<MessageResponse> findBySession(UUID userId, UUID sessionId, int page, int size);

    SseEmitter sendMessage(UUID userId, UUID sessionId, SendMessageRequest request);
}