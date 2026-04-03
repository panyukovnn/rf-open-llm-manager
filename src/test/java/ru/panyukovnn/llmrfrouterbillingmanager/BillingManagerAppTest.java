package ru.panyukovnn.llmrfrouterbillingmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BillingManagerAppTest {

    @Test
    void when_applicationContext_then_loadsSuccessfully() {
        // Контекст загружается без ошибок
    }
}
