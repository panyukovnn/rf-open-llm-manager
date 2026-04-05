package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.MessageMapper;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final SessionService sessionService;

    @Override
    public List<MessageResponse> findBySession(UUID userId, UUID sessionId, int page, int size) {
        sessionService.findById(userId, sessionId);

        return messageRepository.findAllBySessionIdOrderByCreateTimeAsc(sessionId, PageRequest.of(page, size))
                .map(messageMapper::toMessageResponse)
                .getContent();
    }
}