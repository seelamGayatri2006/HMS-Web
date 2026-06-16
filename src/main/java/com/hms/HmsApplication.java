package com.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HmsApplication.class, args);
        System.out.println("HMS Web App running at: http://localhost:8080");
    }
}
