package com.jwt.example.dto;

import com.jwt.example.entity.Role;
import com.jwt.example.entity.User;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<String> role;

    public static UserDto build(User user) {

        Set<Role> roles = user.getRoles();
        Set<String> strRoles = new HashSet<>();

        roles.forEach(role1 -> strRoles.add(role1.getName().name()));

        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                strRoles);
    }
}
