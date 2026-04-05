package ru.panyukovnn.llmrfrouterbillingmanager.property;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "billing-manager.integration")
public class IntegrationProperty {

    private int defaultTimeoutMs = 10_000;

    @Valid
    private LiteLlm liteLlm = new LiteLlm();

    @Getter
    @Setter
    public static class LiteLlm {

        @NotBlank
        private String host;
        private String masterKey;
        @NotBlank
        private String callbackSecret;
    }
}
