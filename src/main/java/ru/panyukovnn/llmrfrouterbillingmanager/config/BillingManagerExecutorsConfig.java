package ru.panyukovnn.llmrfrouterbillingmanager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class BillingManagerExecutorsConfig {

    @Bean
    @ConditionalOnMissingBean(name = "schedulerExecutor")
    public Executor schedulerExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
