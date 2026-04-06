package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageManager;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageManagerImpl implements MessageManager {

    private final MessageService messageService;
    private final AppUserService appUserService;

    @Override
    public List<MessageResponse> handleFindSessionMessages(UUID sessionId, int page, int size) {
        UUID userId = appUserService.findCurrentUser().getId();

        return messageService.findBySession(userId, sessionId, page, size);
    }

    @Override
    public SseEmitter handleSendMessage(UUID sessionId, SendMessageRequest request) {
        UUID userId = appUserService.findCurrentUser().getId();

        return messageService.sendMessage(userId, sessionId, request);
    }
}