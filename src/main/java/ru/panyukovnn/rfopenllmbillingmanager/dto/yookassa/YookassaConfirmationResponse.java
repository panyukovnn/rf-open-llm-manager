package ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa;

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
