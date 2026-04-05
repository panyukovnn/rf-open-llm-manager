package ru.panyukovnn.rfopenllmbillingmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LitellmKeyGenerateRequest {

    private List<String> models;
    private String duration;
    private BigDecimal maxBudget;
    private Map<String, String> metadata;
    private String userId;
}
