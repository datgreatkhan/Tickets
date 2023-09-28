package com.khan.tickets.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CORS not enabled by default for some dumb reason
                .cors(Customizer.withDefaults())
                .csrf().disable()
                // CSRF disabled, if want reenabled, must do:
                // https://devdojo.com/ketonemaniac/doing-spring-securitys-csrf-tokens-the-right-way-with-react
                .authorizeHttpRequests(r -> r
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)));

        return httpSecurity.build();
    }
}
