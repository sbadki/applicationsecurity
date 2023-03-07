package com.jwt.example.repository;

import com.jwt.example.entity.ERole;
import com.jwt.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
