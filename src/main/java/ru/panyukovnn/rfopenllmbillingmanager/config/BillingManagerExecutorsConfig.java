package ru.panyukovnn.rfopenllmbillingmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class BillingManagerExecutorsConfig {

    private static final int CHAT_STREAMING_POOL_SIZE = 16;
    private static final int CHAT_STREAMING_QUEUE_CAPACITY = 512;
    private static final long CHAT_STREAMING_KEEP_ALIVE_SECONDS = 60L;

    @Bean
    public Executor subscriptionExpirationJobExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public Executor chatStreamingExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CHAT_STREAMING_POOL_SIZE,
                CHAT_STREAMING_POOL_SIZE,
                CHAT_STREAMING_KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(CHAT_STREAMING_QUEUE_CAPACITY)
        );
        executor.allowCoreThreadTimeOut(true);

        return executor;
    }
}
