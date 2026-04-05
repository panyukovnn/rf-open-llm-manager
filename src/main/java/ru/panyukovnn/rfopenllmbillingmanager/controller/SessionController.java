package ru.panyukovnn.rfopenllmbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionListItemResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SessionResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.service.SessionManager;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final SessionManager sessionManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<SessionResponse> createSession(
            @Valid @RequestBody CommonRequest<CreateSessionRequest> request) {
        SessionResponse response = sessionManager.handleCreateSession(request.getData());

        return CommonResponse.<SessionResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping
    public CommonResponse<CommonItemsResponse<SessionListItemResponse>> findCurrentUserSessions(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        List<SessionListItemResponse> sessions = sessionManager.handleFindCurrentUserSessions(page, size);

        CommonItemsResponse<SessionListItemResponse> itemsResponse = CommonItemsResponse.<SessionListItemResponse>builder()
                .items(sessions)
                .build();

        return CommonResponse.<CommonItemsResponse<SessionListItemResponse>>builder()
                .data(itemsResponse)
                .build();
    }

    @GetMapping("/{sessionId}")
    public CommonResponse<SessionResponse> findSessionById(@PathVariable UUID sessionId) {
        SessionResponse response = sessionManager.handleFindSessionById(sessionId);

        return CommonResponse.<SessionResponse>builder()
                .data(response)
                .build();
    }

    @PatchMapping("/{sessionId}")
    public CommonResponse<SessionResponse> updateSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CommonRequest<UpdateSessionRequest> request) {
        SessionResponse response = sessionManager.handleUpdateSession(sessionId, request.getData());

        return CommonResponse.<SessionResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<Void> deleteSession(@PathVariable UUID sessionId) {
        sessionManager.handleDeleteSession(sessionId);

        return CommonResponse.<Void>builder()
                .build();
    }
}
