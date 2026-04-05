package ru.panyukovnn.rfopenllmbillingmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UserSubscription extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Идентификатор пользователя
     */
    private UUID appUserId;
    /**
     * Идентификатор тарифного плана
     */
    private UUID subscriptionPlanId;
    /**
     * Статус подписки
     */
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    /**
     * Количество использованных токенов
     */
    private Long tokensUsed;
    /**
     * Дата начала периода подписки
     */
    private Instant periodStart;
    /**
     * Дата окончания периода подписки
     */
    private Instant periodEnd;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        UserSubscription that = (UserSubscription) object;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
