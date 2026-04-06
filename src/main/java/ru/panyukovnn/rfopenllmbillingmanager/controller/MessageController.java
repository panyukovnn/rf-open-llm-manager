package ru.panyukovnn.rfopenllmbillingmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageResponse;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageManager;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonItemsResponse;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class MessageController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final MessageManager messageManager;

    @GetMapping("/{sessionId}/messages")
    public CommonResponse<CommonItemsResponse<MessageResponse>> findSessionMessages(
            @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        List<MessageResponse> messages = messageManager.handleFindSessionMessages(sessionId, page, size);

        CommonItemsResponse<MessageResponse> itemsResponse = CommonItemsResponse.<MessageResponse>builder()
                .items(messages)
                .build();

        return CommonResponse.<CommonItemsResponse<MessageResponse>>builder()
                .data(itemsResponse)
                .build();
    }

    @PostMapping(path = "/{sessionId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(
            @PathVariable UUID sessionId,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CommonRequest<SendMessageRequest> request) {
        SendMessageRequest payload = request.getData();

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            payload.setIdempotencyKey(idempotencyKey);
        }

        return messageManager.handleSendMessage(sessionId, payload);
    }
}