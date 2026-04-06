package ru.panyukovnn.rfopenllmbillingmanager.service;

import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    SessionResponse create(UUID userId, CreateSessionRequest request);

    SessionResponse findById(UUID userId, UUID sessionId);

    Session findEntityById(UUID userId, UUID sessionId);

    void touchLastUpdateTime(UUID sessionId);

    List<SessionListItemResponse> findUserSessions(UUID userId, int page, int size);

    SessionResponse update(UUID userId, UUID sessionId, UpdateSessionRequest request);

    void delete(UUID userId, UUID sessionId);
}
