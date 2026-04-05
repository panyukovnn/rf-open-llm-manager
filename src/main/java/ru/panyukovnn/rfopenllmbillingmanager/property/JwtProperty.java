package ru.panyukovnn.rfopenllmbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "billing-manager.jwt")
public class JwtProperty {

    private String secretKey;
    private int expirationMinutes;
}
