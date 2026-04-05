package ru.panyukovnn.rfopenllmbillingmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.rfopenllmbillingmanager.AbstractTest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.CreateSessionRequest;
import ru.panyukovnn.rfopenllmbillingmanager.dto.UpdateSessionRequest;
import ru.panyukovnn.referencemodelstarter.dto.request.CommonRequest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionControllerTest extends AbstractTest {

    private static final UUID OWNER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final UUID OWNED_SESSION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FOREIGN_SESSION_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateSession {

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user.sql")
        void when_createSession_then_success() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");
            CommonRequest<CreateSessionRequest> request = buildRequest(CreateSessionRequest.builder()
                    .title("Новый чат")
                    .model("gpt-4o")
                    .systemPrompt("ты помощник")
                    .build());

            mockMvc.perform(post("/api/v1/sessions")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.title").value("Новый чат"))
                    .andExpect(jsonPath("$.data.model").value("gpt-4o"))
                    .andExpect(jsonPath("$.data.systemPrompt").value("ты помощник"));
        }

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user.sql")
        void when_createSession_withBlankTitle_then_badRequest() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");
            CommonRequest<CreateSessionRequest> request = buildRequest(CreateSessionRequest.builder()
                    .title(" ")
                    .model("gpt-4o")
                    .build());

            mockMvc.perform(post("/api/v1/sessions")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void when_createSession_withoutToken_then_unauthorized() throws Exception {
            CommonRequest<CreateSessionRequest> request = buildRequest(CreateSessionRequest.builder()
                    .title("t")
                    .model("gpt-4o")
                    .build());

            mockMvc.perform(post("/api/v1/sessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class FindSessions {

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_findCurrentUserSessions_then_returnsOnlyOwnSessions() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

            mockMvc.perform(get("/api/v1/sessions")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.items").isArray())
                    .andExpect(jsonPath("$.data.items.length()").value(2));
        }

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_findSessionById_withOwnedId_then_success() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

            mockMvc.perform(get("/api/v1/sessions/" + OWNED_SESSION_ID)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(OWNED_SESSION_ID.toString()))
                    .andExpect(jsonPath("$.data.title").value("owned first"));
        }

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_findSessionById_withForeignId_then_businessError() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

            mockMvc.perform(get("/api/v1/sessions/" + FOREIGN_SESSION_ID)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    class UpdateSession {

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_updateSession_then_fieldsPatched() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");
            CommonRequest<UpdateSessionRequest> request = buildRequest(UpdateSessionRequest.builder()
                    .title("renamed")
                    .build());

            mockMvc.perform(patch("/api/v1/sessions/" + OWNED_SESSION_ID)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("renamed"))
                    .andExpect(jsonPath("$.data.model").value("gpt-4o"));
        }
    }

    @Nested
    class DeleteSession {

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_deleteSession_then_noContent() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

            mockMvc.perform(delete("/api/v1/sessions/" + OWNED_SESSION_ID)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }

        @Test
        @Transactional
        @Sql("classpath:sql/controller/session/app_user_with_sessions.sql")
        void when_deleteSession_withForeignId_then_businessError() throws Exception {
            String token = jwtTokenProvider.generateToken(OWNER_ID, "owner@example.com");

            mockMvc.perform(delete("/api/v1/sessions/" + FOREIGN_SESSION_ID)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().is4xxClientError());
        }
    }

    private <T> CommonRequest<T> buildRequest(T data) {
        CommonRequest<T> request = new CommonRequest<>();
        request.setData(data);

        return request;
    }
}
