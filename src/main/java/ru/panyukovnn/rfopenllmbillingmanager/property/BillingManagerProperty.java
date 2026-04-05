package ru.panyukovnn.rfopenllmbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "billing-manager")
public class BillingManagerProperty {

    private String frontendRedirectUrl;
}
