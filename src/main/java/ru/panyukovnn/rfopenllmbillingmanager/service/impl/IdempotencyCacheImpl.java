package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.service.IdempotencyCache;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class IdempotencyCacheImpl implements IdempotencyCache {

    private final ChatProperty chatProperty;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<UUID> find(UUID userId, String idempotencyKey) {
        expirePassedEntries();
        CacheEntry entry = cache.get(cacheKey(userId, idempotencyKey));

        if (entry == null) {
            return Optional.empty();
        }

        return Optional.of(entry.messageId);
    }

    @Override
    public void store(UUID userId, String idempotencyKey, UUID messageId) {
        expirePassedEntries();
        Instant expiresAt = Instant.now()
                .plus(chatProperty.getIdempotencyTtlMinutes(), ChronoUnit.MINUTES);
        cache.put(cacheKey(userId, idempotencyKey), new CacheEntry(messageId, expiresAt));
    }

    private String cacheKey(UUID userId, String idempotencyKey) {
        return userId + "::" + idempotencyKey;
    }

    /**
     * Удаляет из кэша записи с истёкшим TTL
     */
    private void expirePassedEntries() {
        Instant now = Instant.now();
        cache.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
    }

    private record CacheEntry(UUID messageId, Instant expiresAt) {
    }
}
