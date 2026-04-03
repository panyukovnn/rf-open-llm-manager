package ru.panyukovnn.llmrfrouterbillingmanager.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.UserSubscriptionRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Scheduled(cron = "${billing-manager.subscription.expiration-cron:0 0 3 * * *}")
    @Transactional
    public void expireSubscriptions() {
        List<UserSubscription> expiredSubscriptions = userSubscriptionRepository
                .findAllByStatusAndPeriodEndBefore(SubscriptionStatus.ACTIVE, Instant.now());

        for (UserSubscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            userSubscriptionRepository.save(subscription);

            log.info("Подписка {} пользователя {} переведена в статус EXPIRED",
                    subscription.getId(), subscription.getAppUserId());
        }

        if (!expiredSubscriptions.isEmpty()) {
            log.info("Обработано истёкших подписок: {}", expiredSubscriptions.size());
        }
    }
}
