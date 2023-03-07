package com.jwt.example.service;

import com.jwt.example.config.JwtTokenUtil;
import com.jwt.example.dto.*;
import com.jwt.example.entity.ERole;
import com.jwt.example.entity.RefreshToken;
import com.jwt.example.entity.Role;
import com.jwt.example.entity.User;
import com.jwt.example.exception.RefreshTokenException;
import com.jwt.example.repository.RoleRepository;
import com.jwt.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    public static final String ROLE_DOES_NOT_EXIST = "Role doesn't exist!";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtil jwtUtils;
    private final PasswordEncoder encoder;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority()).collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthenticationResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .build();
    }


    public MessageResponse register(SignupRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            return new MessageResponse(request.getUsername() + " already exist.");
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse(request.getEmail() + " already exist.");
        }

        //Create new user
        User user = new User(request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword()));

        Set<String> userRoles = request.getRole();
        Set<Role> roles = new HashSet<>();

        if (null == userRoles) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException(ROLE_DOES_NOT_EXIST));
            roles.add(userRole);
        } else {
            userRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_DOES_NOT_EXIST));
                        roles.add(adminRole);
                        break;

                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException(ROLE_DOES_NOT_EXIST));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(ROLE_DOES_NOT_EXIST));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    public Optional<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        return Optional.ofNullable(refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new RefreshTokenResponse(refreshToken, accessToken, user.getEmail());
                }).orElseThrow(() -> new RefreshTokenException(refreshToken, "No refreshToken found")));

    }
}
