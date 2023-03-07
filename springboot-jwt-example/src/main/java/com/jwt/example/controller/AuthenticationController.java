package com.jwt.example.controller;

import com.jwt.example.dto.AuthenticationRequest;
import com.jwt.example.dto.MessageResponse;
import com.jwt.example.dto.RefreshTokenRequest;
import com.jwt.example.dto.SignupRequest;
import com.jwt.example.service.AuthenticationService;
import com.jwt.example.service.RefreshTokenService;
import com.jwt.example.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * /api/v1/auth/signup      - signup new account
 * /api/v1/auth/signin      - login an account
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        int noOfEntries = refreshTokenService.deleteByUserId(userId);
        if(noOfEntries > 0) {
            return ResponseEntity.ok(new MessageResponse("Log out successful!"));
        }
        return ResponseEntity.ok(new MessageResponse("No entry found"));
    }
}
