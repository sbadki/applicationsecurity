package com.oauth2.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello(Principal principal) {
            return "Hello " + principal.getName() + ", This is the Oauth2 demo project!";
    }
}
