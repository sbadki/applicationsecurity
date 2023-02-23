package com.ratelimit.controller;

import com.ratelimit.model.User;
import com.ratelimit.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Bucket bucket;

    public UserController() {
//        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
//        this.bucket = Bucket4j.builder()
//                .addLimit(limit)
//                .build();

        this.bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
                .build();

//        Instant firstRefillTime = ZonedDateTime.now()
//                .truncatedTo(ChronoUnit.HOURS)
//                .plus(1, ChronoUnit.HOURS)
//                .toInstant();
//
//        Bandwidth.classic(400, Refill.intervallyAligned(400, Duration.ofHours(1), firstRefillTime, true));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if(bucket.tryConsume(1)) {
            User user1 = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user1);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getSingleUser(@PathVariable String userId) {
        logger.info("Get Single User Handler: UserController");
        if(bucket.tryConsume(1)) {
            User user = userService.getUser(userId);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        if(bucket.tryConsume(1)) {
            List<User> allUser = userService.getAllUser();
            return ResponseEntity.ok(allUser);
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
