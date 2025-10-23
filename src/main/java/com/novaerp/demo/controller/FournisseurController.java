package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/stock/fournisseurs")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public String listFournisseurs(Model model) {
        model.addAttribute("fournisseurs", fournisseurService.findAll());
        return "stock/fournisseurs/list";
    }

    @GetMapping("/new")
    public String newFournisseur(Model model) {
        model.addAttribute("fournisseur", new Fournisseur());
        return "stock/fournisseurs/form";
    }

    @GetMapping("/edit/{id}")
    public String editFournisseur(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("fournisseur", fournisseurService.findById(id));
            return "stock/fournisseurs/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fournisseur non trouvé");
            return "redirect:/stock/fournisseurs";
        }
    }

    @PostMapping("/save")
    public String saveFournisseur(@Valid @ModelAttribute Fournisseur fournisseur,
                                  BindingResult result,
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "stock/fournisseurs/form";
        }

        fournisseurService.save(fournisseur, auth.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Fournisseur enregistré avec succès");
        return "redirect:/stock/fournisseurs";
    }

    @GetMapping("/delete/{id}")
    public String deleteFournisseur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fournisseurService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Fournisseur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression");
        }
        return "redirect:/stock/fournisseurs";
    }
}