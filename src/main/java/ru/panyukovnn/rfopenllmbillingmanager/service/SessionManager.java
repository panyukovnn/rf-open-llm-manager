package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;

import java.util.List;
import java.util.UUID;

public interface SessionManager {

    SessionResponse handleCreateSession(CreateSessionRequest request);

    SessionResponse handleFindSessionById(UUID sessionId);

    List<SessionListItemResponse> handleFindCurrentUserSessions(int page, int size);

    SessionResponse handleUpdateSession(UUID sessionId, UpdateSessionRequest request);

    void handleDeleteSession(UUID sessionId);
}
