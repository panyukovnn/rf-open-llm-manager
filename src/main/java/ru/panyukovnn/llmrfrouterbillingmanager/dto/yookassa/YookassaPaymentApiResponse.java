package ru.panyukovnn.llmrfrouterbillingmanager.dto.yookassa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YookassaPaymentApiResponse {

    private String id;
    private String status;
    private YookassaConfirmationResponse confirmation;
}
