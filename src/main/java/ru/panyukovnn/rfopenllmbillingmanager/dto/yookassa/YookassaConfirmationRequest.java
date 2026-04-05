package ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YookassaConfirmationRequest {

    private final String type;
    @JsonProperty("return_url")
    private final String returnUrl;
}
