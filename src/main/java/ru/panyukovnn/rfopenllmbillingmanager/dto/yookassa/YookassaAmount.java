package ru.panyukovnn.rfopenllmbillingmanager.dto.yookassa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YookassaAmount {

    private final String value;
    private final String currency;
}
