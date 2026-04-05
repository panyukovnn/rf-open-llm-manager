package ru.panyukovnn.rfopenllmbillingmanager.config;

import org.junit.jupiter.api.Test;
import ru.panyukovnn.rfopenllmbillingmanager.AbstractTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CorsConfigTest extends AbstractTest {

    @Test
    void when_corsPreflight_withAllowedOrigin_then_allowed() throws Exception {
        mockMvc.perform(options("/api/v1/users/me")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void when_corsPreflight_withDisallowedOrigin_then_forbidden() throws Exception {
        mockMvc.perform(options("/api/v1/users/me")
                        .header("Origin", "http://evil.example.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
