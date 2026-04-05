package ru.panyukovnn.rfopenllmbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "billing-manager.yookassa")
public class YookassaProperty {

    private String shopId;
    private String secretKey;
    private String returnUrl;
    private String apiUrl = "https://api.yookassa.ru/v3";
    private List<String> allowedWebhookIps = List.of();
}
