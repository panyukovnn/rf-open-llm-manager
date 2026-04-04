package ru.panyukovnn.llmrfrouterbillingmanager.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.llmrfrouterbillingmanager.property.IntegrationProperty;

import java.time.Duration;

@Configuration
public class BillingManagerRestClientConfig {

    @Bean
    public RestClient liteLlmRestClient(RestClient.Builder restClientBuilder,
                                        IntegrationProperty integrationProperty) {
        IntegrationProperty.LiteLlm liteLlm = integrationProperty.getLiteLlm();
        Duration timeout = Duration.ofMillis(integrationProperty.getDefaultTimeoutMs());

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(timeout)
                .withReadTimeout(timeout);

        return restClientBuilder
                .baseUrl(liteLlm.getHost())
                .defaultHeader("Authorization", "Bearer " + liteLlm.getMasterKey())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }
}
