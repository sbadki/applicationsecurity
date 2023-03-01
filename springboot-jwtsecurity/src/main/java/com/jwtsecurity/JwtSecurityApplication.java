package com.jwtsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"com.jwtsecurity.entity"})
@SpringBootApplication
public class JwtSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtSecurityApplication.class, args);
    }
}
