package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "users/form";
    }

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
            model.addAttribute("error", "Erreur lors de la création : " + e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
            return "redirect:/users";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("categories", userService.getCategoriesByUser(id));
            model.addAttribute("fournisseurs", userService.getFournisseursByUser(id));
            return "users/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
            return "redirect:/users";
        }
    }

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
            model.addAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            return "users/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication auth) {
        try {
            User user = userService.findByEmail(auth.getName());
            model.addAttribute("user", user);
            model.addAttribute("categories", userService.getCategoriesByUser(user.getId()));
            model.addAttribute("fournisseurs", userService.getFournisseursByUser(user.getId()));
            return "users/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du chargement du profil");
            return "error";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute User user,
                                BindingResult result,
                                Authentication auth,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            User currentUser = userService.findByEmail(auth.getName());
            model.addAttribute("user", currentUser);
            return "users/profile";
        }

        try {
            User currentUser = userService.findByEmail(auth.getName());
            user.setRoles(currentUser.getRoles());
            userService.updateProfile(auth.getName(), user);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
            return "redirect:/users/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la mise à jour : " + e.getMessage());
            User currentUser = userService.findByEmail(auth.getName());
            model.addAttribute("user", currentUser);
            return "users/profile";
        }
    }
}
