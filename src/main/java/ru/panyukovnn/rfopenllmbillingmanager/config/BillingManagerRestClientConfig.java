package ru.panyukovnn.rfopenllmbillingmanager.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.rfopenllmbillingmanager.property.IntegrationProperty;
import ru.panyukovnn.rfopenllmbillingmanager.property.YookassaProperty;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

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

    @Bean
    public RestClient yookassaRestClient(RestClient.Builder restClientBuilder,
                                         IntegrationProperty integrationProperty,
                                         YookassaProperty yookassaProperty) {
        Duration timeout = Duration.ofMillis(integrationProperty.getDefaultTimeoutMs());
        String credentials = yookassaProperty.getShopId() + ":" + yookassaProperty.getSecretKey();
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(timeout)
                .withReadTimeout(timeout);

        return restClientBuilder
                .baseUrl(yookassaProperty.getApiUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }
}
