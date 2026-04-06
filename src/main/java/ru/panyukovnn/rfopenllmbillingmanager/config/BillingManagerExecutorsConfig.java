package ru.panyukovnn.rfopenllmbillingmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class BillingManagerExecutorsConfig {

    @Bean
    public Executor subscriptionExpirationJobExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public Executor chatStreamingExecutor() {
        return Executors.newCachedThreadPool();
    }
}
