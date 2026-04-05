package ru.panyukovnn.rfopenllmbillingmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BillingManagerApp {

    private BillingManagerApp() {
    }

    public static void main(String[] args) {
        SpringApplication.run(BillingManagerApp.class, args);
    }
}
