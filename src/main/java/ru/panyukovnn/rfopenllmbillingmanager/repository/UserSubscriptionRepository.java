package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionStatus;
import ru.panyukovnn.rfopenllmbillingmanager.model.UserSubscription;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

    Optional<UserSubscription> findByAppUserIdAndStatus(UUID appUserId, SubscriptionStatus status);

    List<UserSubscription> findAllByStatusAndPeriodEndBefore(SubscriptionStatus status, Instant periodEnd);
}
