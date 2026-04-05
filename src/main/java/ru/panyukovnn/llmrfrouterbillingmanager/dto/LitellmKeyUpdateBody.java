package ru.panyukovnn.llmrfrouterbillingmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LitellmKeyUpdateBody {

    private String key;
    @JsonProperty("max_budget")
    private BigDecimal maxBudget;
    @JsonProperty("tpm_limit")
    private Long tpmLimit;
}