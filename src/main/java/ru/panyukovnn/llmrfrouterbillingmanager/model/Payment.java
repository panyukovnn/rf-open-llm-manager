package ru.panyukovnn.llmrfrouterbillingmanager.model;

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

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment extends AuditableEntity {

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
     * Идентификатор платежа в ЮKassa
     */
    private String yookassaPaymentId;
    /**
     * Сумма платежа в копейках
     */
    private Long amountKopecks;
    /**
     * Статус платежа
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Payment payment = (Payment) object;

        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
