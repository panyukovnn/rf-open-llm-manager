package ru.panyukovnn.llmrfrouterbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "billing-manager.yookassa")
public class YookassaProperty {

    private String shopId;
    private String secretKey;
    private String returnUrl;
}
