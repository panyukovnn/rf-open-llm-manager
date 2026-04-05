package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findAllBySessionIdOrderByCreateTimeAsc(UUID sessionId, Pageable pageable);

    List<Message> findTop50BySessionIdOrderByCreateTimeDesc(UUID sessionId);

    long countBySessionId(UUID sessionId);
}