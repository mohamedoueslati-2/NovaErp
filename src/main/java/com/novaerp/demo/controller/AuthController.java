package com.novaerp.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Le constructeur et le UserRepository ne sont plus nécessaires ici
    // car ils n'étaient pas utilisés.

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        // Spring injecte automatiquement l'objet Authentication de l'utilisateur connecté
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
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