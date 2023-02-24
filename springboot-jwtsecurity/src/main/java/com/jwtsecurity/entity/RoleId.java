package com.jwtsecurity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleId {
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "role", nullable = false)
    private String role;
}
