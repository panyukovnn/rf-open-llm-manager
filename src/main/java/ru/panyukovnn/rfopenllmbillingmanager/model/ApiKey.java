package ru.panyukovnn.rfopenllmbillingmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ApiKey extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Идентификатор пользователя
     */
    private UUID appUserId;
    /**
     * Хеш API-ключа
     */
    private String keyHash;
    /**
     * Идентификатор ключа в LiteLLM
     */
    private String litellmKeyId;
    /**
     * Наименование ключа
     */
    private String name;
    /**
     * Признак активности ключа
     */
    private Boolean active;
    /**
     * Время отзыва ключа
     */
    private Instant revokedAt;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ApiKey apiKey = (ApiKey) object;

        return Objects.equals(id, apiKey.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
