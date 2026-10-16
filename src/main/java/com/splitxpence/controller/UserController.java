package com.splitxpence.controller;

import com.splitxpence.model.User;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(
            UserRepository userRepository) {

        this.userRepository =
                userRepository;
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        Optional<User> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()) {

            return "Email already registered";
        }

        User user =
                new User(
                        name,
                        email,
                        password
                );

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Optional<User> userOptional =
                userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {

            return "User not found";
        }

        User user =
                userOptional.get();

        if (!user.getPassword()
                .equals(password)) {

            return "Incorrect password";
        }

        session.setAttribute(
                "userId",
                user.getId()
        );

        session.setAttribute(
                "userName",
                user.getName()
        );

        return "Login successful. Welcome "
                + user.getName();
    }

    @GetMapping("/logout")
    public void logout(
            HttpSession session,
            HttpServletResponse response)
            throws IOException {

        session.invalidate();

        response.sendRedirect(
                "/html/login.html"
        );
    }
}