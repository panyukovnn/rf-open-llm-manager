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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UsageEvent extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Идентификатор API-ключа
     */
    private UUID apiKeyId;
    /**
     * Идентификатор пользователя
     */
    private UUID appUserId;
    /**
     * Наименование модели
     */
    private String model;
    /**
     * Количество токенов запроса
     */
    private Long promptTokens;
    /**
     * Количество токенов ответа
     */
    private Long completionTokens;
    /**
     * Общее количество токенов
     */
    private Long totalTokens;
    /**
     * Стоимость вызова в долларах
     */
    private BigDecimal costUsd;
    /**
     * Идентификатор вызова в LiteLLM
     */
    private String litellmCallId;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        UsageEvent that = (UsageEvent) object;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
