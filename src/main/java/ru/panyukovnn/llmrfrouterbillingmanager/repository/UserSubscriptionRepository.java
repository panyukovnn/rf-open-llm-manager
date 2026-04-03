package ru.panyukovnn.llmrfrouterbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.llmrfrouterbillingmanager.model.UserSubscription;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

    Optional<UserSubscription> findByAppUserIdAndStatus(UUID appUserId, SubscriptionStatus status);

    List<UserSubscription> findAllByStatusAndPeriodEndBefore(SubscriptionStatus status, Instant periodEnd);
}
