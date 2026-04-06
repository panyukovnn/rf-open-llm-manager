package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageManager {

    List<MessageResponse> handleFindSessionMessages(UUID sessionId, int page, int size);

    SseEmitter handleSendMessage(UUID sessionId, SendMessageRequest request);
}