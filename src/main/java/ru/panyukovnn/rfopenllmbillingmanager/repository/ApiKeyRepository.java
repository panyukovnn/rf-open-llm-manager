package ru.panyukovnn.rfopenllmbillingmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.rfopenllmbillingmanager.model.ApiKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    List<ApiKey> findAllByAppUserIdAndActiveTrue(UUID appUserId);

    Optional<ApiKey> findByKeyHash(String keyHash);

    Optional<ApiKey> findByLitellmKeyId(String litellmKeyId);
}
