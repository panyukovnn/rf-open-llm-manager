package ru.panyukovnn.rfopenllmbillingmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LitellmUsageCallbackPayload {

    private String id;
    @JsonProperty("call_type")
    private String callType;
    @JsonProperty("user_api_key_hash")
    private String userApiKeyHash;
    private String model;
    @JsonProperty("prompt_tokens")
    private Long promptTokens;
    @JsonProperty("completion_tokens")
    private Long completionTokens;
    @JsonProperty("total_tokens")
    private Long totalTokens;
    @JsonProperty("response_cost")
    private BigDecimal responseCost;
    private String status;
    private Instant startTime;
    private Instant endTime;
}