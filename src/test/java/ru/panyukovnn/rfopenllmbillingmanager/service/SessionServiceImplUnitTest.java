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
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.SessionMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.repository.SessionRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.SessionServiceImpl;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplUnitTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Nested
    class Create {

        @Test
        void when_createSession_then_sessionPersistedAndResponseReturned() {
            UUID userId = UUID.randomUUID();
            CreateSessionRequest request = CreateSessionRequest.builder()
                    .title("Новый чат")
                    .model("gpt-4o")
                    .systemPrompt("ты помощник")
                    .build();
            UUID savedId = UUID.randomUUID();

            when(sessionRepository.save(any(Session.class)))
                    .thenAnswer(invocation -> {
                        Session session = invocation.getArgument(0);
                        session.setId(savedId);

                        return session;
                    });
            when(sessionMapper.toSessionResponse(any(Session.class)))
                    .thenAnswer(invocation -> {
                        Session session = invocation.getArgument(0);

                        return SessionResponse.builder()
                                .id(session.getId())
                                .title(session.getTitle())
                                .model(session.getModel())
                                .systemPrompt(session.getSystemPrompt())
                                .build();
                    });

            SessionResponse response = sessionService.create(userId, request);

            assertEquals(savedId, response.getId());
            assertEquals("Новый чат", response.getTitle());
            assertEquals("gpt-4o", response.getModel());
            assertEquals("ты помощник", response.getSystemPrompt());
            verify(sessionRepository).save(any(Session.class));
        }
    }

    @Nested
    class FindById {

        @Test
        void when_findById_withOwnedSession_then_returnsResponse() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, userId, "мой чат");

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.of(session));
            when(sessionMapper.toSessionResponse(session))
                    .thenReturn(SessionResponse.builder()
                            .id(sessionId)
                            .title("мой чат")
                            .build());

            SessionResponse response = sessionService.findById(userId, sessionId);

            assertEquals(sessionId, response.getId());
            assertEquals("мой чат", response.getTitle());
        }

        @Test
        void when_findById_withForeignSession_then_businessException() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> sessionService.findById(userId, sessionId));
        }
    }

    @Nested
    class FindUserSessions {

        @Test
        void when_findUserSessions_then_returnsOnlyOwnSessions() {
            UUID userId = UUID.randomUUID();
            Session first = buildSession(UUID.randomUUID(), userId, "первый");
            Session second = buildSession(UUID.randomUUID(), userId, "второй");
            Page<Session> page = new PageImpl<>(List.of(first, second));

            when(sessionRepository.findAllByUserIdOrderByLastUpdateTimeDesc(
                    any(UUID.class), any(Pageable.class)))
                    .thenReturn(page);
            when(sessionMapper.toSessionListItemResponse(any(Session.class)))
                    .thenAnswer(invocation -> {
                        Session session = invocation.getArgument(0);

                        return SessionListItemResponse.builder()
                                .id(session.getId())
                                .title(session.getTitle())
                                .build();
                    });

            List<SessionListItemResponse> items = sessionService.findUserSessions(userId, 0, 20);

            assertEquals(2, items.size());
            assertEquals("первый", items.get(0).getTitle());
            assertEquals("второй", items.get(1).getTitle());
        }
    }

    @Nested
    class Update {

        @Test
        void when_updateSession_then_onlyNonNullFieldsPatched() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, userId, "начальный");
            session.setModel("gpt-4o");
            session.setSystemPrompt("prompt v1");
            UpdateSessionRequest request = UpdateSessionRequest.builder()
                    .title("обновлённый")
                    .build();

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.of(session));
            when(sessionRepository.save(session))
                    .thenReturn(session);
            when(sessionMapper.toSessionResponse(session))
                    .thenAnswer(invocation -> {
                        Session src = invocation.getArgument(0);

                        return SessionResponse.builder()
                                .id(src.getId())
                                .title(src.getTitle())
                                .model(src.getModel())
                                .systemPrompt(src.getSystemPrompt())
                                .build();
                    });

            SessionResponse response = sessionService.update(userId, sessionId, request);

            assertEquals("обновлённый", response.getTitle());
            assertEquals("gpt-4o", response.getModel());
            assertEquals("prompt v1", response.getSystemPrompt());
        }

        @Test
        void when_updateSession_withMissingSession_then_businessException() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> sessionService.update(userId, sessionId, UpdateSessionRequest.builder().title("x").build()));

            verify(sessionRepository, never()).save(any(Session.class));
        }
    }

    @Nested
    class Delete {

        @Test
        void when_deleteSession_then_sessionRemoved() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();
            Session session = buildSession(sessionId, userId, "на удаление");

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.of(session));

            sessionService.delete(userId, sessionId);

            verify(sessionRepository).delete(session);
        }

        @Test
        void when_deleteSession_withForeignSession_then_businessException() {
            UUID userId = UUID.randomUUID();
            UUID sessionId = UUID.randomUUID();

            when(sessionRepository.findByIdAndUserId(sessionId, userId))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> sessionService.delete(userId, sessionId));

            verify(sessionRepository, never()).delete(any(Session.class));
        }
    }

    private Session buildSession(UUID id, UUID userId, String title) {
        Session session = Session.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .model("gpt-4o")
                .build();
        session.setCreateTime(Instant.now());
        session.setLastUpdateTime(Instant.now());

        return session;
    }
}
