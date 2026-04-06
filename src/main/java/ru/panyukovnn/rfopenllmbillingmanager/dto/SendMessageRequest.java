package ru.panyukovnn.rfopenllmbillingmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotBlank
    private String content;
    /**
     * Идемпотентный идентификатор запроса — защищает от повторной обработки
     */
    private String idempotencyKey;
}
