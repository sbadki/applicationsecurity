package com.ratelimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserRateLimiter {
    public static void main(String[] args) {
        SpringApplication.run(UserRateLimiter.class);
    }
}
