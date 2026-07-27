package com.immiauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImmigrationAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImmigrationAutomationApplication.class, args);
    }
}
