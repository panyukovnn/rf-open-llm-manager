package ru.panyukovnn.llmrfrouterbillingmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class LitellmKeyGenerateResponse {

    private String key;
    @JsonProperty("key_name")
    private String keyId;
    @JsonProperty("expires")
    private Instant expiresAt;
}
