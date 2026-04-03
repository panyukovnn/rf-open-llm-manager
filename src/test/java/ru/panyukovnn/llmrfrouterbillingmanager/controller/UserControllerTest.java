package ru.panyukovnn.llmrfrouterbillingmanager.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.panyukovnn.llmrfrouterbillingmanager.AbstractTest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractTest {

    @Nested
    class FindCurrentUserProfile {

        @Test
        @Transactional
        @Sql("classpath:sql/controller/user/me/app_user.sql")
        void when_findCurrentUserProfile_then_success() throws Exception {
            UUID userId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
            String token = jwtTokenProvider.generateToken(userId, "test@example.com");

            mockMvc.perform(get("/api/v1/users/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"));
        }

        @Test
        void when_findCurrentUserProfile_withInvalidToken_then_unauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/users/me")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void when_findCurrentUserProfile_withoutToken_then_unauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
