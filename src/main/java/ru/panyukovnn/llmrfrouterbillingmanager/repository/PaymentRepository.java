package ru.panyukovnn.llmrfrouterbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByYookassaPaymentId(String yookassaPaymentId);

    List<Payment> findAllByAppUserId(UUID appUserId);
}
