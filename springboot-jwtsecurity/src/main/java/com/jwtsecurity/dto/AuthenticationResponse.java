package com.jwtsecurity.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

//    public AuthenticationResponse(String jwt, Long id, String username, String email, List<String> roles) {
//        this.token = jwt;
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.roles = roles;
//        this.type =
//    }
}
