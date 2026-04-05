package ru.panyukovnn.rfopenllmbillingmanager.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final UserSubscriptionService userSubscriptionService;

    @Async("subscriptionExpirationJobExecutor")
    @Scheduled(cron = "${billing-manager.scheduler.subscription-expiration-job.cron:0 0 3 * * *}")
    @SchedulerLock(name = "expireSubscriptions")
    public CompletableFuture<Void> expireSubscriptions() {
        log.info("Запуск задачи истечения подписок");

        int expiredCount = userSubscriptionService.expireSubscriptions();

        log.info("Задача истечения подписок завершена, обработано: {}", expiredCount);

        return CompletableFuture.completedFuture(null);
    }
}
