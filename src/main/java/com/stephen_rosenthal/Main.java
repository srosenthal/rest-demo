package com.stephen_rosenthal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.stephen_rosenthal")
@EnableAutoConfiguration
public class Main {

    /**
     * Entry point for the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
