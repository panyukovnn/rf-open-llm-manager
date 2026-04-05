package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.llmrfrouterbillingmanager.dto.LitellmUsageCallbackPayload;
import ru.panyukovnn.llmrfrouterbillingmanager.property.IntegrationProperty;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UsageTrackingService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/usage-callback")
@RequiredArgsConstructor
public class UsageCallbackController {

    private static final String CALLBACK_SECRET_HEADER = "X-Callback-Secret";

    private final UsageTrackingService usageTrackingService;
    private final IntegrationProperty integrationProperty;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> handleCallback(
            @RequestHeader(value = CALLBACK_SECRET_HEADER, required = false) String callbackSecret,
            @RequestBody List<LitellmUsageCallbackPayload> payloads) {
        if (!isSecretValid(callbackSecret)) {
            log.warn("Получен usage callback с недопустимым секретом");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        usageTrackingService.processUsageCallback(payloads);

        return ResponseEntity.ok(CommonResponse.<Void>builder().build());
    }

    private boolean isSecretValid(String callbackSecret) {
        String expectedSecret = integrationProperty.getLiteLlm().getCallbackSecret();

        return expectedSecret.equals(callbackSecret);
    }
}
