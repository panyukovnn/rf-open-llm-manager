package ru.panyukovnn.rfopenllmbillingmanager.service;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyCache {

    Optional<UUID> find(UUID userId, String idempotencyKey);

    void store(UUID userId, String idempotencyKey, UUID messageId);
}
