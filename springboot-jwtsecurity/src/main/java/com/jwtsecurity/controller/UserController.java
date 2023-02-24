package com.jwtsecurity.controller;

import com.jwtsecurity.dto.UserDto;
import com.jwtsecurity.entity.User;
import com.jwtsecurity.service.UserDetailsServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserDetailsServiceImpl userService;

    private final ModelMapper modelMapper;

    @GetMapping("/test")
    public String allAccess() {
        return "Hello, All can access this page !!!";
    }

//    @GetMapping("/user")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
//    public String userAccess(Principal principal) {
//        return "Hello, " + principal.getName();
//    }
//
//    @GetMapping("/mod")
//    @PreAuthorize("hasRole('MODERATOR')")
//    public String moderatorAccess(Principal principal) {
//        return "Hello, " + principal.getName();
//    }
//
//    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String adminAccess(Principal principal) {
//        return "Hello, " + principal.getName();
//    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<?>> getAllUsers() {
         return ResponseEntity.ok()
                 .body(userService.getAllUsers()
                         .stream().map(user -> modelMapper.map(user, UserDto.class))
                         .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserById(@NonNull @PathVariable Long id) {
        User user = userService.getUserById(id);

        //UserDto userDtoResponse = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('MODERATOR')")
    //public ResponseEntity<?> updateUser(@NonNull @RequestBody UserDto userDto) {
    public ResponseEntity<?> updateUser(@NonNull @RequestBody User user) {
        //Convert DTO to entity
        //User userRequest = modelMapper.map(userDto, User.class);

       User updateUser = userService.updateUser(user);
//
//        //Convert Entity to DTO
//        UserDto userDtoUpdated = modelMapper.map(user, UserDto.class);

        return ResponseEntity.ok().body(updateUser);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@NonNull @PathVariable Long id) {
        userService.deleteUser(id);
    }
}
