package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/profile/edit")
    public String editProfileSubmit(@RequestParam String nom,
                                    @RequestParam String prenom,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        User currentUser = getAuthenticatedUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!currentUser.getEmail().equals(email.trim().toLowerCase())) {
            if (userService.findByEmail(email.trim().toLowerCase()).isPresent()) {
                model.addAttribute("error", "Cet email est déjà utilisé !");
                model.addAttribute("user", currentUser);
                return "user/edit";
            }
        }

        // Mise à jour des informations
        currentUser.setNom(nom);
        currentUser.setPrenom(prenom);
        currentUser.setEmail(email.trim().toLowerCase());
        currentUser.setPassword(password);

        User updatedUser = userService.update(currentUser);
        session.setAttribute("user", updatedUser);

        return "redirect:/user/profile";
    }

    // Méthode utilitaire pour récupérer l'utilisateur connecté
    private User getAuthenticatedUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userService.findById(userId).orElse(null);
    }
}