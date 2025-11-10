package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie; // Importation ajoutée
import com.novaerp.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "roles/list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roleTypes", RoleTypologie.values()); // Ajout des types de rôles
        return "roles/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.findById(id);
            model.addAttribute("role", role);
            model.addAttribute("roleTypes", RoleTypologie.values()); // Ajout des types de rôles
            return "roles/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Rôle non trouvé pour l'ID : " + id);
            return "redirect:/roles";
        }
    }

    @PostMapping("/save")
    public String saveRole(@Valid @ModelAttribute("role") Role role,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roleTypes", RoleTypologie.values()); // Ajout en cas d'erreur
            return "roles/form";
        }

        try {
            roleService.save(role);
            String message = (role.getId() == null) ? "Rôle créé avec succès" : "Rôle modifié avec succès";
            redirectAttributes.addFlashAttribute("success", message);
            return "redirect:/roles";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement du rôle : " + e.getMessage());
            model.addAttribute("roleTypes", RoleTypologie.values()); // Ajout en cas d'exception
            return "roles/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Le rôle a été supprimé avec succès.");
        } catch (EmptyResultDataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer : Rôle non trouvé pour l'ID " + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du rôle : " + e.getMessage());
        }
        return "redirect:/roles";
    }
}