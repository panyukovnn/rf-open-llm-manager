package ru.panyukovnn.llmrfrouterbillingmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageEventResponse {

    private String model;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Instant createdAt;
}