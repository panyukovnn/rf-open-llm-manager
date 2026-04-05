package ru.panyukovnn.rfopenllmbillingmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.AbstractTest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageControllerTest extends AbstractTest {

    private static final UUID OWNER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID OWNED_SESSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FOREIGN_SESSION_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    @Transactional
    @Sql("classpath:sql/controller/message/app_user_with_messages.sql")
    void when_findSessionMessages_withOwnedSession_then_success() throws Exception {
        String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

        mockMvc.perform(get("/api/v1/sessions/" + OWNED_SESSION_ID + "/messages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(3))
                .andExpect(jsonPath("$.data.items[0].content").value("hello"))
                .andExpect(jsonPath("$.data.items[0].role").value("USER"))
                .andExpect(jsonPath("$.data.items[1].content").value("hi there"))
                .andExpect(jsonPath("$.data.items[2].content").value("how are you"));
    }

    @Test
    @Transactional
    @Sql("classpath:sql/controller/message/app_user_with_messages.sql")
    void when_findSessionMessages_withForeignSession_then_businessError() throws Exception {
        String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

        mockMvc.perform(get("/api/v1/sessions/" + FOREIGN_SESSION_ID + "/messages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    @Sql("classpath:sql/controller/message/app_user_with_messages.sql")
    void when_findSessionMessages_withPagination_then_respectsPageSize() throws Exception {
        String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

        mockMvc.perform(get("/api/v1/sessions/" + OWNED_SESSION_ID + "/messages")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(2));
    }
}