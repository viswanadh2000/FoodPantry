package com.pantrypulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PantryPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PantryPulseApplication.class, args);
    }

}
