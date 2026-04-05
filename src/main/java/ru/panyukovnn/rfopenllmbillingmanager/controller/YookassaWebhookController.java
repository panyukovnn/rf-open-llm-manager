package ru.panyukovnn.rfopenllmbillingmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.rfopenllmbillingmanager.dto.YookassaWebhookPayload;
import ru.panyukovnn.rfopenllmbillingmanager.property.YookassaProperty;
import ru.panyukovnn.rfopenllmbillingmanager.service.PaymentService;
import ru.panyukovnn.referencemodelstarter.dto.response.CommonResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/yookassa-webhook")
@RequiredArgsConstructor
public class YookassaWebhookController {

    private final PaymentService paymentService;
    private final YookassaProperty yookassaProperty;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> handleWebhook(
            HttpServletRequest httpRequest,
            @RequestBody YookassaWebhookPayload payload) {
        String remoteIp = httpRequest.getRemoteAddr();

        if (!isAllowedIp(remoteIp)) {
            log.warn("Получен webhook от ЮKassa с недопустимого IP {}", remoteIp);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        paymentService.processWebhook(payload);

        return ResponseEntity.ok(CommonResponse.<Void>builder().build());
    }

    private boolean isAllowedIp(String remoteIp) {
        List<String> allowedIps = yookassaProperty.getAllowedWebhookIps();

        if (allowedIps == null || allowedIps.isEmpty()) {
            return true;
        }

        return allowedIps.contains(remoteIp);
    }
}
