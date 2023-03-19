package com.oauth2.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService inMemoryUsers() {
        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();

        var alice = new User("alice", passwordEncoder().encode("pass123"), Collections.EMPTY_LIST);
        //var bob = new User("bob", passwordEncoder().encode("pass321"), Collections.EMPTY_LIST) ;

        var bob = User
                .builder()
                .username("bob")                                            //AuthN
                .password(passwordEncoder().encode("pass321"))   //AuthN
                .roles("USER")          //AuthZ
                .authorities("read")    //AuthZ
                .build();

        users.createUser(alice);
        users.createUser(bob);
        return users;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(withDefaults())  //which provide normal login option
                .oauth2Login(withDefaults()) //which provide login to github/facebook
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();

    }

    @Bean
    ApplicationListener<AuthenticationSuccessEvent> successLogger() {
        return event -> {
            log.info("Success {}" , event.getAuthentication());
        };
    }
}
