package com.jwtsecurity.service;

import com.jwtsecurity.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long userId);

    List<User> getAllUsers();

    User updateUserEmail(Long id, User user);

    void deleteUser(Long userId);
}
