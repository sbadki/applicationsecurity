package com.jwtsecurity.service;

import com.jwtsecurity.entity.User;
import com.jwtsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder encoder;

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User doesn't exist :" + userId)));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println(users);
        return users;
    }

    @Override
    public User updateUserEmail(Long id, User userRequest) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User doesn't exist with id :" + id));

        log.info("User exists : " +user);

        if(null != userRequest.getEmail()) {
            user.setEmail(userRequest.getEmail());
        }
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist :" + userId));

        log.info("User {} with id {} to be deleted " , user.getUsername(), user.getId());
        int noOfEntries = refreshTokenService.deleteByUserId(user.getId());
        log.info("No of entries deleted : " +noOfEntries);
        if(noOfEntries > 0) {
            userRepository.delete(user);
        }
    }
}
