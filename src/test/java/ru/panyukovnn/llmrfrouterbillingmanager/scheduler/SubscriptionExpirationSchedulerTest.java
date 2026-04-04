package ru.panyukovnn.llmrfrouterbillingmanager.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.llmrfrouterbillingmanager.AbstractTest;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubscriptionExpirationSchedulerTest extends AbstractTest {

    private static final UUID EXPIRED_SUBSCRIPTION_ID = UUID.fromString("a221bb7b-3f76-4e0a-a4d2-ddf74f0d1502");
    private static final UUID ACTIVE_SUBSCRIPTION_ID = UUID.fromString("3fdba915-40d2-499d-be46-c3ac95652e9f");

    @Test
    @Transactional
    @Sql("classpath:sql/scheduler/subscriptionexpiration/expire-subscriptions/user_subscription.sql")
    void when_expireSubscriptions_then_expiredSubscriptionsUpdatedAndActiveSubscriptionsUntouched() {
        subscriptionExpirationScheduler.expireSubscriptions();

        UserSubscription expiredSubscription = userSubscriptionRepository.findById(EXPIRED_SUBSCRIPTION_ID)
                .orElseThrow();

        assertEquals(SubscriptionStatus.EXPIRED, expiredSubscription.getStatus());

        UserSubscription activeSubscription = userSubscriptionRepository.findById(ACTIVE_SUBSCRIPTION_ID)
                .orElseThrow();

        assertEquals(SubscriptionStatus.ACTIVE, activeSubscription.getStatus());
    }
}
