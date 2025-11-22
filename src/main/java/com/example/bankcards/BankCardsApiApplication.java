package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BankCardsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankCardsApiApplication.class, args);
    }
}