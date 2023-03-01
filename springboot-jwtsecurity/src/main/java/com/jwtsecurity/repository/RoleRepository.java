package com.jwtsecurity.repository;

import com.jwtsecurity.entity.ERole;
import com.jwtsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
