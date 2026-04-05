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

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubscriptionPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Наименование тарифного плана
     */
    private String name;
    /**
     * Описание тарифного плана
     */
    private String description;
    /**
     * Месячный лимит токенов
     */
    private Long monthlyTokenLimit;
    /**
     * Стоимость в копейках
     */
    private Long priceKopecks;
    /**
     * Признак активности тарифного плана
     */
    private Boolean active;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        SubscriptionPlan that = (SubscriptionPlan) object;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
