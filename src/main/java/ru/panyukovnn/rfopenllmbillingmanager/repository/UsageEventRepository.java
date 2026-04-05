package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.UsageEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UsageEventRepository extends JpaRepository<UsageEvent, UUID> {

    List<UsageEvent> findAllByAppUserIdAndCreateTimeBetween(UUID appUserId, Instant from, Instant to);
}