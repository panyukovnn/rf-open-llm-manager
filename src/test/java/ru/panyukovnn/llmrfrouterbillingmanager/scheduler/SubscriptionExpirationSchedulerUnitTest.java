package ru.panyukovnn.llmrfrouterbillingmanager.scheduler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.UserSubscriptionRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionExpirationSchedulerUnitTest {

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;

    @InjectMocks
    private SubscriptionExpirationScheduler subscriptionExpirationScheduler;

    @Nested
    class ExpireSubscriptions {

        @Test
        void when_expirationScheduler_withExpiredSubscription_then_statusChangedToExpired() {
            UserSubscription subscription = UserSubscription.builder()
                    .id(UUID.randomUUID())
                    .appUserId(UUID.randomUUID())
                    .status(SubscriptionStatus.ACTIVE)
                    .periodEnd(Instant.now().minus(1, ChronoUnit.DAYS))
                    .build();

            when(userSubscriptionRepository.findAllByStatusAndPeriodEndBefore(
                    eq(SubscriptionStatus.ACTIVE), any(Instant.class)))
                    .thenReturn(List.of(subscription));
            when(userSubscriptionRepository.save(any(UserSubscription.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            subscriptionExpirationScheduler.expireSubscriptions();

            assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
            verify(userSubscriptionRepository).save(subscription);
        }

        @Test
        void when_expirationScheduler_withNoExpiredSubscriptions_then_noChanges() {
            when(userSubscriptionRepository.findAllByStatusAndPeriodEndBefore(
                    eq(SubscriptionStatus.ACTIVE), any(Instant.class)))
                    .thenReturn(Collections.emptyList());

            subscriptionExpirationScheduler.expireSubscriptions();

            verify(userSubscriptionRepository, never()).save(any());
        }
    }
}
