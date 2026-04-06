package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.mapper.SessionMapper;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.repository.SessionRepository;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Override
    public SessionResponse create(UUID userId, CreateSessionRequest request) {
        Session session = Session.builder()
                .userId(userId)
                .title(request.getTitle())
                .model(request.getModel())
                .systemPrompt(request.getSystemPrompt())
                .build();
        Session saved = sessionRepository.save(session);

        log.info("Чат {} создан для пользователя {}", saved.getId(), userId);

        return sessionMapper.toSessionResponse(saved);
    }

    @Override
    public SessionResponse findById(UUID userId, UUID sessionId) {
        Session session = findOwnedOrThrow(userId, sessionId);

        return sessionMapper.toSessionResponse(session);
    }

    @Override
    public Session findEntityById(UUID userId, UUID sessionId) {
        return findOwnedOrThrow(userId, sessionId);
    }

    @Override
    public void touchLastUpdateTime(UUID sessionId) {
        sessionRepository.findById(sessionId)
                .ifPresent(session -> {
                    session.setLastUpdateTime(Instant.now());
                    sessionRepository.save(session);
                });
    }

    @Override
    public List<SessionListItemResponse> findUserSessions(UUID userId, int page, int size) {
        return sessionRepository.findAllByUserIdOrderByLastUpdateTimeDesc(userId, PageRequest.of(page, size))
                .map(sessionMapper::toSessionListItemResponse)
                .getContent();
    }

    @Override
    public SessionResponse update(UUID userId, UUID sessionId, UpdateSessionRequest request) {
        Session session = findOwnedOrThrow(userId, sessionId);
        applyUpdate(session, request);
        Session saved = sessionRepository.save(session);

        log.info("Чат {} обновлён пользователем {}", sessionId, userId);

        return sessionMapper.toSessionResponse(saved);
    }

    @Override
    public void delete(UUID userId, UUID sessionId) {
        Session session = findOwnedOrThrow(userId, sessionId);
        sessionRepository.delete(session);

        log.info("Чат {} удалён пользователем {}", sessionId, userId);
    }

    /**
     * Находит чат, принадлежащий пользователю, иначе — BusinessException
     */
    private Session findOwnedOrThrow(UUID userId, UUID sessionId) {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new BusinessException(
                        "s001",
                        "Чат не найден"));
    }

    /**
     * Применяет частичное обновление чата — пропускает null-поля
     */
    private void applyUpdate(Session session, UpdateSessionRequest request) {
        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }

        if (request.getModel() != null) {
            session.setModel(request.getModel());
        }

        if (request.getSystemPrompt() != null) {
            session.setSystemPrompt(request.getSystemPrompt());
        }
    }
}
