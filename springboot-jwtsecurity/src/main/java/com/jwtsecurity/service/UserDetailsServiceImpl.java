package com.jwtsecurity.service;

import com.jwtsecurity.entity.User;
import com.jwtsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username : " + username));
        return UserDetailsImpl.build(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User doesn't exist :" + userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User userRequest) {
        String username = userRequest.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User doesn't exist :" + username));

        user.setPassword(userRequest.getPassword());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setRoles(new HashSet<>(userRequest.getRoles()));
        return userRepository.save(user);

    }


    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist :" + userId));

        userRepository.delete(user);
    }
}
