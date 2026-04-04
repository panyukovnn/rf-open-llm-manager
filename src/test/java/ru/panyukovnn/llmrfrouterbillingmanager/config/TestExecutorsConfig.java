package ru.panyukovnn.llmrfrouterbillingmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Profile("test")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestExecutorsConfig {

    @Bean
    public Executor schedulerExecutor() {
        return new SyncTaskExecutor();
    }
}
