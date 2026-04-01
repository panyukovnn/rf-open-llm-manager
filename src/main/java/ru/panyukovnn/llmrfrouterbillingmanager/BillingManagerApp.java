package ru.panyukovnn.llmrfrouterbillingmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingManagerApp {

    private BillingManagerApp() {
    }

    public static void main(String[] args) {
        SpringApplication.run(BillingManagerApp.class, args);
    }
}
