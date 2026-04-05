package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {

    List<SubscriptionPlan> findAllByActiveTrue();
}
