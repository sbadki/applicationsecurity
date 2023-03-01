package com.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SignupRequest {

    private String username;
    private String password;
    private String email;
    private Set<String> role;
}
