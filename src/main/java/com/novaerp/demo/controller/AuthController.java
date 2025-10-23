package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "redirect:/users/profile";
    }



    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}