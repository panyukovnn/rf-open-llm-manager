package ru.panyukovnn.llmrfrouterbillingmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.panyukovnn.llmrfrouterbillingmanager.config.JwtTokenProvider;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.AppUserRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.repository.UserSubscriptionRepository;
import ru.panyukovnn.llmrfrouterbillingmanager.scheduler.SubscriptionExpirationScheduler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected JwtTokenProvider jwtTokenProvider;
    @Autowired
    protected AppUserRepository appUserRepository;
    @Autowired
    protected UserSubscriptionRepository userSubscriptionRepository;
    @Autowired
    protected SubscriptionExpirationScheduler subscriptionExpirationScheduler;
}
