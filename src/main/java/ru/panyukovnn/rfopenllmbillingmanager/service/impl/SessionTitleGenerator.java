package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.panyukovnn.rfopenllmbillingmanager.client.LitellmClient;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.repository.SessionRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionTitleGenerator {

    private static final String TITLE_PROMPT = """
            Придумай короткое название (3-5 слов) для чата на основе сообщения пользователя и ответа ассистента. \
            Ответь ТОЛЬКО названием, без кавычек, без пояснений.""";
    private static final int MAX_TITLE_LENGTH = 60;

    private final LitellmClient litellmClient;
    private final SessionRepository sessionRepository;
    private final ChatProperty chatProperty;

    /**
     * Генерирует название сессии через LLM на основе первого обмена сообщениями
     */
    public void generateTitle(String virtualKey, Session session, String userMessage, String assistantMessage) {
        try {
            String contextMessage = "Пользователь: " + truncate(userMessage, 200)
                    + "\nАссистент: " + truncate(assistantMessage, 200);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(chatProperty.getTitleGenerationModel())
                    .messages(List.of(
                            ChatMessage.builder().role("system").content(TITLE_PROMPT).build(),
                            ChatMessage.builder().role("user").content(contextMessage).build()
                    ))
                    .maxTokens(30)
                    .temperature(0.7)
                    .build();

            String title = litellmClient.chatCompletion(virtualKey, request);

            if (title != null && !title.isBlank()) {
                title = title.strip();

                if (title.length() > MAX_TITLE_LENGTH) {
                    title = title.substring(0, MAX_TITLE_LENGTH);
                }

                session.setTitle(title);
                sessionRepository.save(session);
                log.info("Название сессии {} обновлено: '{}'", session.getId(), title);
            }
        } catch (Exception e) {
            log.warn("Не удалось сгенерировать название сессии {}: {}", session.getId(), e.getMessage());
        }
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "…";
    }
}
