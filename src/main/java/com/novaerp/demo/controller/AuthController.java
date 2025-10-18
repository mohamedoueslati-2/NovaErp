package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Rediriger si déjà connecté
        if (session.getAttribute("user") != null) {
            return "redirect:/user/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // Normalisation email pour éviter les espaces ou majuscules
        User user = userService.login(email.trim().toLowerCase(), password);

        if (user == null) {
            model.addAttribute("error", "Email ou mot de passe incorrect !");
            return "login";
        }

        // Sauvegarde user en session
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole().getName().name());

        // Redirection selon rôle
        RoleTypologie role = user.getRole().getName();
        switch (role) {
            case ADMIN:
            case STOCK:
            case VENTE:
                return "redirect:/user/dashboard";
            default:
                session.invalidate();
                model.addAttribute("error", "Rôle invalide !");
                return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}