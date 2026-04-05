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

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Идентификатор чата, к которому относится сообщение
     */
    private UUID sessionId;
    /**
     * Роль автора сообщения
     */
    @Enumerated(EnumType.STRING)
    private MessageRole role;
    /**
     * Содержимое сообщения
     */
    private String content;
    /**
     * Количество входящих токенов
     */
    private Integer tokensIn;
    /**
     * Количество исходящих токенов
     */
    private Integer tokensOut;
    /**
     * Модель LLM, использованная для генерации
     */
    private String model;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Message message = (Message) object;

        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}