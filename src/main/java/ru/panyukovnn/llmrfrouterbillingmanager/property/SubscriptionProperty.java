package ru.panyukovnn.llmrfrouterbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.subscription")
public class SubscriptionProperty {

    private int gracePeriodDays;
}
