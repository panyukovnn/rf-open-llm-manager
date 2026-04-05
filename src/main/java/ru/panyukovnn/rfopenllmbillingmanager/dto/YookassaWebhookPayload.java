package ru.panyukovnn.rfopenllmbillingmanager.dto;

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
public class YookassaWebhookPayload {

    private String event;
    private PaymentObject object;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentObject {

        private String id;
        private String status;
        private Map<String, String> metadata;
    }
}