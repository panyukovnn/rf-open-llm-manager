package ru.panyukovnn.rfopenllmbillingmanager.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;
import ru.panyukovnn.rfopenllmbillingmanager.model.Message;
import ru.panyukovnn.rfopenllmbillingmanager.model.MessageRole;
import ru.panyukovnn.rfopenllmbillingmanager.model.Session;
import ru.panyukovnn.rfopenllmbillingmanager.property.ChatProperty;
import ru.panyukovnn.rfopenllmbillingmanager.service.impl.ContextBuilderImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextBuilderImplUnitTest {

    @Mock
    private ChatProperty chatProperty;

    @InjectMocks
    private ContextBuilderImpl contextBuilder;

    @Nested
    class Build {

        @Test
        void when_buildContext_withEmptyHistory_then_onlyUserMessage() {
            when(chatProperty.getMaxHistoryMessages()).thenReturn(20);
            Session session = buildSession(null);

            List<ChatMessage> context = contextBuilder.build(session, List.of(), "привет");

            assertEquals(1, context.size());
            assertEquals("user", context.get(0).getRole());
            assertEquals("привет", context.get(0).getContent());
        }

        @Test
        void when_buildContext_withSystemPrompt_then_systemPromptFirstInContext() {
            when(chatProperty.getMaxHistoryMessages()).thenReturn(20);
            Session session = buildSession("ты помощник");

            List<ChatMessage> context = contextBuilder.build(session, List.of(), "вопрос");

            assertEquals(2, context.size());
            assertEquals("system", context.get(0).getRole());
            assertEquals("ты помощник", context.get(0).getContent());
            assertEquals("user", context.get(1).getRole());
        }

        @Test
        void when_buildContext_withLongHistory_then_contextTruncated() {
            when(chatProperty.getMaxHistoryMessages()).thenReturn(2);
            Session session = buildSession(null);
            List<Message> history = new ArrayList<>();
            history.add(buildMessage("первое", MessageRole.USER, Instant.parse("2026-04-01T10:00:00Z")));
            history.add(buildMessage("второе", MessageRole.ASSISTANT, Instant.parse("2026-04-01T10:01:00Z")));
            history.add(buildMessage("третье", MessageRole.USER, Instant.parse("2026-04-01T10:02:00Z")));

            List<ChatMessage> context = contextBuilder.build(session, history, "новое");

            assertEquals(3, context.size());
            assertEquals("второе", context.get(0).getContent());
            assertEquals("третье", context.get(1).getContent());
            assertEquals("новое", context.get(2).getContent());
        }
    }

    private Session buildSession(String systemPrompt) {
        return Session.builder()
                .id(UUID.randomUUID())
                .model("gpt-4o")
                .systemPrompt(systemPrompt)
                .build();
    }

    private Message buildMessage(String content, MessageRole role, Instant createTime) {
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .role(role)
                .content(content)
                .build();
        message.setCreateTime(createTime);

        return message;
    }
}
