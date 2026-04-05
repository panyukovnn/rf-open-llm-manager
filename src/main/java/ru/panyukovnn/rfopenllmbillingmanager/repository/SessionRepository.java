package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    Page<Session> findAllByUserIdOrderByLastUpdateTimeDesc(UUID userId, Pageable pageable);

    Optional<Session> findByIdAndUserId(UUID id, UUID userId);
}
