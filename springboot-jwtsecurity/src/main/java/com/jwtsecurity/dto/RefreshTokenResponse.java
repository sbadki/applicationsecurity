package com.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshTokenResponse {
    private String email;
    private String accessToken;
    private String type = "Bearer";
    private String refreshToken;

    public RefreshTokenResponse(String refreshToken, String accessToken, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
