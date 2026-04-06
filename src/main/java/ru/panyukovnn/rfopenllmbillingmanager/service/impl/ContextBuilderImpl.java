package ru.panyukovnn.rfopenllmbillingmanager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.service.ContextBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ContextBuilderImpl implements ContextBuilder {

    private static final String SYSTEM_ROLE = "system";
    private static final String USER_ROLE = "user";

    private final ChatProperty chatProperty;

    @Override
    public List<ChatMessage> build(Session session, List<Message> history, String userContent) {
        List<Message> truncatedHistory = truncate(history);
        List<ChatMessage> context = new ArrayList<>();
        String systemPrompt = session.getSystemPrompt();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            context.add(ChatMessage.builder()
                    .role(SYSTEM_ROLE)
                    .content(systemPrompt)
                    .build());
        }

        for (Message historyItem : truncatedHistory) {
            context.add(toChatMessage(historyItem));
        }

        context.add(ChatMessage.builder()
                .role(USER_ROLE)
                .content(userContent)
                .build());

        return context;
    }

    /**
     * Ограничивает историю последними maxHistoryMessages элементами
     */
    private List<Message> truncate(List<Message> history) {
        int limit = chatProperty.getMaxHistoryMessages();

        if (history.size() <= limit) {
            return history;
        }

        return history.subList(history.size() - limit, history.size());
    }

    private ChatMessage toChatMessage(Message message) {
        return ChatMessage.builder()
                .role(message.getRole().name().toLowerCase(Locale.ROOT))
                .content(message.getContent())
                .build();
    }
}
