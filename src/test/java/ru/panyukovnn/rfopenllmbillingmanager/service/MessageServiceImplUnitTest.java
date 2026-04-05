package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.MessageMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.repository.MessageRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.MessageServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplUnitTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Nested
    class FindBySession {

        @Test
        void when_findBySession_then_returnsItemsOrderedByCreateTime() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Message first = buildMessage(UUID.randomUUID(), sessionId, "первое", Instant.parse("2026-04-01T10:00:00Z"));
            Message second = buildMessage(UUID.randomUUID(), sessionId, "второе", Instant.parse("2026-04-01T11:00:00Z"));
            Page<Message> page = new PageImpl<>(List.of(first, second));

            when(messageRepository.findAllBySessionIdOrderByCreateTimeAsc(eq(sessionId), any(Pageable.class)))
                    .thenReturn(page);
            when(messageMapper.toMessageResponse(any(Message.class)))
                    .thenAnswer(invocation -> {
                        Message src = invocation.getArgument(0);

                        return MessageResponse.builder()
                                .id(src.getId())
                                .content(src.getContent())
                                .createTime(src.getCreateTime())
                                .build();
                    });

            List<MessageResponse> items = messageService.findBySession(userId, sessionId, 0, 20);

            assertEquals(2, items.size());
            assertEquals("первое", items.get(0).getContent());
            assertEquals("второе", items.get(1).getContent());
            verify(sessionService).findById(userId, sessionId);
        }

        @Test
        void when_findBySession_withForeignSession_then_businessException() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            doThrow(new BusinessException("s001", "Чат не найден"))
                    .when(sessionService).findById(userId, sessionId);

            assertThrows(BusinessException.class,
                    () -> messageService.findBySession(userId, sessionId, 0, 20));

            verify(messageRepository, never()).findAllBySessionIdOrderByCreateTimeAsc(any(), any());
        }

        @Test
        void when_findBySession_withEmptySession_then_emptyList() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(messageRepository.findAllBySessionIdOrderByCreateTimeAsc(eq(sessionId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            List<MessageResponse> items = messageService.findBySession(userId, sessionId, 0, 20);

            assertTrue(items.isEmpty());
        }
    }

    private Message buildMessage(UUID id, UUID sessionId, String content, Instant createTime) {
        Message message = Message.builder()
                .id(id)
                .sessionId(sessionId)
                .role(MessageRole.USER)
                .content(content)
                .build();
        message.setCreateTime(createTime);
        message.setLastUpdateTime(createTime);

        return message;
    }
}