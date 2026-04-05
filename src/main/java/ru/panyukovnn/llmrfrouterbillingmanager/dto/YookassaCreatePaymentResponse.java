package ru.panyukovnn.llmrfrouterbillingmanager.dto;

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
public class YookassaCreatePaymentResponse {

    private String id;
    private String status;
    private String confirmationUrl;
}