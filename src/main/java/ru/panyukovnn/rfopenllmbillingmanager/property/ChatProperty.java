package ru.panyukovnn.rfopenllmbillingmanager.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.chat")
public class ChatProperty {

    /**
     * Максимальное количество сообщений в контексте, отправляемом в LLM
     */
    private int maxHistoryMessages;
    /**
     * Количество токенов, резервируемое под ответ модели
     */
    private int reserveTokens;
    /**
     * Таймаут SSE-стрима в миллисекундах
     */
    private long sseTimeoutMs;
    /**
     * Время жизни записи идемпотентности
     */
    private long idempotencyTtlMinutes;
}
