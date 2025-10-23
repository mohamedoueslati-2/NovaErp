package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // Liste des rôles
    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "roles/list";
    }

    // Afficher formulaire de création
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roleTypes", RoleTypologie.values());
        return "roles/form";
    }

    // Créer un rôle
    @PostMapping
    public String createRole(@Valid @ModelAttribute Role role,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roleTypes", RoleTypologie.values());
            return "roles/form";
        }

        try {
            roleService.save(role);
            redirectAttributes.addFlashAttribute("success", "Rôle créé avec succès");
            return "redirect:/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roleTypes", RoleTypologie.values());
            return "roles/form";
        }
    }

    // Afficher formulaire de modification
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("role", roleService.findById(id));
        model.addAttribute("roleTypes", RoleTypologie.values());
        return "roles/form";
    }

    // Mettre à jour un rôle
    @PostMapping("/{id}")
    public String updateRole(@PathVariable Long id,
                             @Valid @ModelAttribute Role role,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roleTypes", RoleTypologie.values());
            return "roles/form";
        }

        try {
            roleService.update(id, role);
            redirectAttributes.addFlashAttribute("success", "Rôle modifié avec succès");
            return "redirect:/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roleTypes", RoleTypologie.values());
            return "roles/form";
        }
    }

    // Supprimer un rôle
    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Rôle supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/roles";
    }
}