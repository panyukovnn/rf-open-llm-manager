package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.service.AppUserService;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionManager;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionManagerImpl implements SessionManager {

    private final SessionService sessionService;
    private final AppUserService appUserService;

    @Override
    public SessionResponse handleCreateSession(CreateSessionRequest request) {
        UUID userId = appUserService.findCurrentUser().getId();

        return sessionService.create(userId, request);
    }

    @Override
    public SessionResponse handleFindSessionById(UUID sessionId) {
        UUID userId = appUserService.findCurrentUser().getId();

        return sessionService.findById(userId, sessionId);
    }

    @Override
    public List<SessionListItemResponse> handleFindCurrentUserSessions(int page, int size) {
        UUID userId = appUserService.findCurrentUser().getId();

        return sessionService.findUserSessions(userId, page, size);
    }

    @Override
    public SessionResponse handleUpdateSession(UUID sessionId, UpdateSessionRequest request) {
        UUID userId = appUserService.findCurrentUser().getId();

        return sessionService.update(userId, sessionId, request);
    }

    @Override
    public void handleDeleteSession(UUID sessionId) {
        UUID userId = appUserService.findCurrentUser().getId();
        sessionService.delete(userId, sessionId);
    }
}
