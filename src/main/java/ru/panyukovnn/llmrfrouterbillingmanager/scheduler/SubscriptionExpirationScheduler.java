package ru.panyukovnn.llmrfrouterbillingmanager.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panyukovnn.llmrfrouterbillingmanager.service.UserSubscriptionService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final UserSubscriptionService userSubscriptionService;

    @Async("schedulerExecutor")
    @Scheduled(cron = "${billing-manager.subscription.expiration-cron:0 0 3 * * *}")
    public CompletableFuture<Void> expireSubscriptions() {
        log.info("Запуск задачи истечения подписок");
        int expiredCount = userSubscriptionService.expireSubscriptions();
        log.info("Задача истечения подписок завершена, обработано: {}", expiredCount);

        return CompletableFuture.completedFuture(null);
    }
}
