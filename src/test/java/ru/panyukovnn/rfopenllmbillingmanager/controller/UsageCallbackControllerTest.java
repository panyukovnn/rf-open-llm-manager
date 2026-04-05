package ru.panyukovnn.rfopenllmbillingmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.panyukovnn.rfopenllmbillingmanager.AbstractTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsageCallbackControllerTest extends AbstractTest {

    @Test
    void when_processUsageCallback_withInvalidSecret_then_forbidden() throws Exception {
        String body = "[]";

        mockMvc.perform(post("/api/v1/internal/usage-callback")
                        .header("X-Callback-Secret", "wrong-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }
}
