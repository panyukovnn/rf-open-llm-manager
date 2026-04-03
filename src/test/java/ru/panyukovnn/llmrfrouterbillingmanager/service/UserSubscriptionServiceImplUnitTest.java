package ru.panyukovnn.llmrfrouterbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.llmrfrouterbillingmanager.exception.TokenLimitExceededException;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionPlan;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;
import ru.panyukovnn.llmrfrouterbillingmanager.property.SubscriptionProperty;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.UserSubscriptionRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.service.impl.UserSubscriptionServiceImpl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplUnitTest {

    @Mock
    private UserSubscriptionRepository userSubscriptionRepository;
    @Mock
    private SubscriptionPlanService subscriptionPlanService;
    @Mock
    private SubscriptionProperty subscriptionProperty;

    @InjectMocks
    private UserSubscriptionServiceImpl userSubscriptionService;

    @Nested
    class FindActiveSubscription {

        @Test
        void when_findActiveSubscription_then_success() {
            UUID userId = UUID.randomUUID();
            UserSubscription subscription = UserSubscription.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .status(SubscriptionStatus.ACTIVE)
                    .build();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));

            Optional<UserSubscription> result = userSubscriptionService.findActiveSubscription(userId);

            assertTrue(result.isPresent());
            assertEquals(SubscriptionStatus.ACTIVE, result.get().getStatus());
        }

        @Test
        void when_findActiveSubscription_withNoSubscription_then_emptyResult() {
            UUID userId = UUID.randomUUID();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            Optional<UserSubscription> result = userSubscriptionService.findActiveSubscription(userId);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class ActivateSubscription {

        @Test
        void when_activateSubscription_then_subscriptionCreated() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .name("Standard")
                    .monthlyTokenLimit(5000000L)
                    .build();

            when(subscriptionPlanService.findById(planId)).thenReturn(plan);
            when(subscriptionProperty.getPeriodDays()).thenReturn(30);
            when(userSubscriptionRepository.save(any(UserSubscription.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UserSubscription result = userSubscriptionService.activateSubscription(userId, planId);

            assertNotNull(result);
            assertEquals(userId, result.getAppUserId());
            assertEquals(planId, result.getSubscriptionPlanId());
            assertEquals(SubscriptionStatus.ACTIVE, result.getStatus());
            assertEquals(0L, result.getTokensUsed());
            assertNotNull(result.getPeriodStart());
            assertNotNull(result.getPeriodEnd());
            verify(userSubscriptionRepository).save(any(UserSubscription.class));
        }
    }

    @Nested
    class DeductTokens {

        @Test
        void when_deductTokens_then_tokensUsedIncremented() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            UserSubscription subscription = UserSubscription.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .subscriptionPlanId(planId)
                    .status(SubscriptionStatus.ACTIVE)
                    .tokensUsed(1000L)
                    .build();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .monthlyTokenLimit(5000000L)
                    .build();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionPlanService.findById(planId)).thenReturn(plan);
            when(userSubscriptionRepository.save(any(UserSubscription.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            userSubscriptionService.deductTokens(userId, 500L);

            assertEquals(1500L, subscription.getTokensUsed());
            verify(userSubscriptionRepository).save(subscription);
        }

        @Test
        void when_deductTokens_withExceededLimit_then_tokenLimitExceededException() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            UserSubscription subscription = UserSubscription.builder()
                    .id(UUID.randomUUID())
                    .appUserId(userId)
                    .subscriptionPlanId(planId)
                    .status(SubscriptionStatus.ACTIVE)
                    .tokensUsed(4999900L)
                    .build();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .monthlyTokenLimit(5000000L)
                    .build();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionPlanService.findById(planId)).thenReturn(plan);

            assertThrows(TokenLimitExceededException.class,
                    () -> userSubscriptionService.deductTokens(userId, 200L));
        }

        @Test
        void when_deductTokens_withNoActiveSubscription_then_throwsException() {
            UUID userId = UUID.randomUUID();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class,
                    () -> userSubscriptionService.deductTokens(userId, 100L));
        }
    }

    @Nested
    class HasAvailableTokens {

        @Test
        void when_hasAvailableTokens_withTokensAvailable_then_returnsTrue() {
            UUID userId = UUID.randomUUID();
            UUID planId = UUID.randomUUID();
            UserSubscription subscription = UserSubscription.builder()
                    .appUserId(userId)
                    .subscriptionPlanId(planId)
                    .status(SubscriptionStatus.ACTIVE)
                    .tokensUsed(1000L)
                    .build();
            SubscriptionPlan plan = SubscriptionPlan.builder()
                    .id(planId)
                    .monthlyTokenLimit(5000000L)
                    .build();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));
            when(subscriptionPlanService.findById(planId)).thenReturn(plan);

            assertTrue(userSubscriptionService.hasAvailableTokens(userId));
        }

        @Test
        void when_hasAvailableTokens_withNoSubscription_then_returnsFalse() {
            UUID userId = UUID.randomUUID();

            when(userSubscriptionRepository.findByAppUserIdAndStatus(userId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            assertFalse(userSubscriptionService.hasAvailableTokens(userId));
        }
    }
}
