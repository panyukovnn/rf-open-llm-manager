package ru.panyukovnn.rfopenllmbillingmanager.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import ru.panyukovnn.referencemodelstarter.exception.BusinessException;
import ru.panyukovnn.rfopenllmbillingmanager.client.impl.LitellmClientImpl;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionChunk;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatCompletionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.ChatMessage;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withRawStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.POST;

class LitellmClientImplUnitTest {

    private static final String BASE_URL = "http://localhost:4000";
    private static final String VIRTUAL_KEY = "sk-virtual-xyz";

    private MockRestServiceServer mockServer;
    private LitellmClientImpl litellmClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        litellmClient = new LitellmClientImpl(restClient);
    }

    @Test
    void when_streamCompletion_then_parsesChunksInOrder() {
        String sseBody = """
                data: {"id":"1","choices":[{"index":0,"delta":{"role":"assistant","content":"Hello"},"finish_reason":null}]}

                data: {"id":"1","choices":[{"index":0,"delta":{"content":" world"},"finish_reason":null}]}

                data: {"id":"1","choices":[{"index":0,"delta":{},"finish_reason":"stop"}]}

                data: [DONE]

                """;

        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + VIRTUAL_KEY))
                .andExpect(jsonPath("$.stream").value(true))
                .andExpect(jsonPath("$.model").value("gpt-4o"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(sseBody));

        Iterator<ChatCompletionChunk> iterator = litellmClient.streamCompletion(VIRTUAL_KEY, buildRequest());

        assertTrue(iterator.hasNext());
        ChatCompletionChunk first = iterator.next();
        assertEquals("Hello", first.getChoices().get(0).getDelta().getContent());

        assertTrue(iterator.hasNext());
        ChatCompletionChunk second = iterator.next();
        assertEquals(" world", second.getChoices().get(0).getDelta().getContent());

        assertTrue(iterator.hasNext());
        ChatCompletionChunk third = iterator.next();
        assertEquals("stop", third.getChoices().get(0).getFinishReason());

        assertFalse(iterator.hasNext());
        mockServer.verify();
    }

    @Test
    void when_streamCompletion_withInvalidKey_then_businessException() {
        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalid api key\"}"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> litellmClient.streamCompletion(VIRTUAL_KEY, buildRequest()));

        assertEquals("8d4a", exception.getLocation());
    }

    @Test
    void when_streamCompletion_withBudgetExceeded_then_businessException() {
        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(POST))
                .andRespond(withRawStatus(402)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"budget exceeded\"}"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> litellmClient.streamCompletion(VIRTUAL_KEY, buildRequest()));

        assertEquals("f569", exception.getLocation());
    }

    @Test
    void when_streamCompletion_withUpstream5xx_then_businessException() {
        mockServer.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(POST))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"upstream failure\"}"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> litellmClient.streamCompletion(VIRTUAL_KEY, buildRequest()));

        assertEquals("d9ac", exception.getLocation());
    }

    private ChatCompletionRequest buildRequest() {
        return ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(ChatMessage.builder()
                        .role("user")
                        .content("Привет")
                        .build()))
                .temperature(0.7)
                .maxTokens(256)
                .build();
    }
}