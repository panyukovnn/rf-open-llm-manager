package ru.panyukovnn.llmrfrouterbillingmanager.dto.yookassa;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class YookassaPaymentBody {

    private final YookassaAmount amount;
    private final YookassaConfirmationRequest confirmation;
    private final Boolean capture;
    private final String description;
    private final Map<String, String> metadata;
}
