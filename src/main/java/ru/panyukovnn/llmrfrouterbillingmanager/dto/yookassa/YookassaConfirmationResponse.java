package ru.panyukovnn.llmrfrouterbillingmanager.dto.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YookassaConfirmationResponse {

    private String type;
    @JsonProperty("confirmation_url")
    private String confirmationUrl;
}
