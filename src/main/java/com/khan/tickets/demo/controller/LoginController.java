package com.khan.tickets.demo.controller;

import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    private record UserInfo(OAuth2AuthenticatedPrincipal principalData, User userData) {}

    @GetMapping("/user/me")
    public ResponseEntity<UserInfo> me(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        if(principal != null) {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getAttribute("email"));

            User user;

            if(optionalUser.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }else{
                user = optionalUser.get();
            }

            return new ResponseEntity<>(new UserInfo(principal, user), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/user/login")
    public void login(HttpServletResponse response) {
        try {
            response.sendRedirect("http://localhost:3000/dashboard");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
