package com.travel360.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Travel360ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Travel360ApiApplication.class, args);
    }
} 