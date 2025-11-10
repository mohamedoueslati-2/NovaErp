package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.service.RoleService;
import com.novaerp.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    // Liste des utilisateurs - ADMIN uniquement
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    // Afficher formulaire de création - ADMIN uniquement
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "users/form";
    }

    // Créer un utilisateur - ADMIN uniquement
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(@Valid @ModelAttribute User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }

        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "Utilisateur créé avec succès");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }
    }

    // Afficher formulaire de modification - ADMIN uniquement
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", roleService.findAll());
        return "users/form";
    }

    // Mettre à jour un utilisateur - ADMIN uniquement
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }

        try {
            userService.update(id, user);
            redirectAttributes.addFlashAttribute("success", "Utilisateur modifié avec succès");
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }
    }

    // Supprimer un utilisateur - ADMIN uniquement
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    // Consulter son propre profil - Tous les utilisateurs
    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("user", user);
        return "users/profile";
    }

    // Modifier son propre profil - Tous les utilisateurs
    // Modifier son propre profil - Tous les utilisateurs
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute User user,
                                BindingResult result,
                                Authentication auth,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            // Recharger l'utilisateur pour afficher les rôles
            User currentUser = userService.findByEmail(auth.getName());
            model.addAttribute("user", currentUser);
            return "users/profile";
        }

        try {
            // Récupérer l'utilisateur actuel et préserver ses rôles
            User currentUser = userService.findByEmail(auth.getName());
            user.setRoles(currentUser.getRoles());

            userService.updateProfile(auth.getName(), user);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
            return "redirect:/users/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            // Recharger l'utilisateur pour afficher les rôles
            User currentUser = userService.findByEmail(auth.getName());
            model.addAttribute("user", currentUser);
            return "users/profile";
        }
    }
}