package ru.panyukovnn.rfopenllmbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "billing-manager.cors")
public class CorsProperty {

    private List<String> allowedOrigins = List.of();
}
