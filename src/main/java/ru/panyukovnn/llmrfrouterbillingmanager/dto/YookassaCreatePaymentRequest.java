package ru.panyukovnn.llmrfrouterbillingmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YookassaCreatePaymentRequest {

    private String amountValue;
    private String amountCurrency;
    private String description;
    private String returnUrl;
    private Map<String, String> metadata;
}