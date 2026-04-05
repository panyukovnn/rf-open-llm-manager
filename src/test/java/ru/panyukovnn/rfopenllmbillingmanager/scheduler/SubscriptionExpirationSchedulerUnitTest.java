package ru.panyukovnn.rfopenllmbillingmanager.scheduler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.rfopenllmbillingmanager.service.UserSubscriptionService;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionExpirationSchedulerUnitTest {

    @Mock
    private UserSubscriptionService userSubscriptionService;

    @InjectMocks
    private SubscriptionExpirationScheduler subscriptionExpirationScheduler;

    @Nested
    class ExpireSubscriptions {

        @Test
        void when_expireSubscriptions_then_delegatesToService() throws Exception {
            CompletableFuture<Void> result = subscriptionExpirationScheduler.expireSubscriptions();

            assertNotNull(result);
            assertTrue(result.isDone());
            verify(userSubscriptionService).expireSubscriptions();
        }
    }
}
