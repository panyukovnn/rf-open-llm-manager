package ru.panyukovnn.llmrfrouterbillingmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.panyukovnn.llmrfrouterbillingmanager.model.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private String planName;
    private Long amountKopecks;
    private PaymentStatus status;
    private Instant createdAt;
}
