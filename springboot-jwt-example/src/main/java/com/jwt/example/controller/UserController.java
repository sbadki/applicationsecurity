package com.jwt.example.controller;

import com.jwt.example.entity.User;
import com.jwt.example.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/test")
    public String allAccess() {
        return "Hello, All can access this page !!!";
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<?>> getAllUsers() {
         return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@NonNull @PathVariable Long id) {
        User user = userService.getUserById(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("Bad credential." + id));

        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/mod/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> updateUserEmail(@NonNull @PathVariable Long id,
                                        @NonNull @RequestBody User user) {
       User updateUser = userService.updateUserEmail(id, user);

       return ResponseEntity.ok().body(updateUser);
    }

    /** TO test:
        http://localhost:8080/api/v1/users/4
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@NonNull @PathVariable Long id) {
        userService.deleteUser(Long.valueOf(id));
    }
}
