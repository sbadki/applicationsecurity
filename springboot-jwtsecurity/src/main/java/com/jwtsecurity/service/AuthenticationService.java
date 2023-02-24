package com.jwtsecurity.service;

import com.jwtsecurity.config.JwtTokenUtil;
import com.jwtsecurity.dto.AuthenticationRequest;
import com.jwtsecurity.dto.AuthenticationResponse;
import com.jwtsecurity.dto.SignupRequest;
import com.jwtsecurity.dto.SignupResponse;
import com.jwtsecurity.entity.ERole;
import com.jwtsecurity.entity.Role;
import com.jwtsecurity.entity.User;
import com.jwtsecurity.repository.RoleRepository;
import com.jwtsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    public static final String ROLE_DOES_NOT_EXIST = "Role doesn't exist!";
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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

        return AuthenticationResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .token(jwt).build();
    }


    public SignupResponse register(SignupRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            return new SignupResponse(request.getUsername() + " already exist.");
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            return new SignupResponse(request.getEmail() + " already exist.");
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

        return new SignupResponse("User registered successfully");
    }
}
