package com.jwtsecurity.service;

import com.jwtsecurity.entity.User;

import java.util.List;

public interface UserService {

    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(Long userId);
}
