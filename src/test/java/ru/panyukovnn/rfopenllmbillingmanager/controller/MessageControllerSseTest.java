package ru.panyukovnn.rfopenllmbillingmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.MessageChunkType;
import ru.panyukovnn.rfopenllmbillingmanager.dto.SendMessageRequest;
import ru.panyukovnn.rfopenllmbillingmanager.service.MessageManager;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerSseTest {

    private static final UUID SESSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private MessageManager messageManager;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MessageController(messageManager))
                .build();
    }

    @Test
    void when_sendMessage_then_sseStreamReturned() throws Exception {
        SseEmitter emitter = new SseEmitter(120000L);
        when(messageManager.handleSendMessage(eq(SESSION_ID), any(SendMessageRequest.class)))
                .thenAnswer(invocation -> {
                    emitter.send(MessageChunk.builder()
                            .type(MessageChunkType.TOKEN).content("Ответ").build());
                    emitter.send(MessageChunk.builder()
                            .type(MessageChunkType.DONE).messageId(UUID.randomUUID()).build());
                    emitter.complete();

                    return emitter;
                });

        CommonRequest<SendMessageRequest> request = CommonRequest.<SendMessageRequest>builder()
                .data(SendMessageRequest.builder().content("привет").build())
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/sessions/" + SESSION_ID + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        String response = mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("TOKEN"));
        assertTrue(response.contains("DONE"));
    }
}
