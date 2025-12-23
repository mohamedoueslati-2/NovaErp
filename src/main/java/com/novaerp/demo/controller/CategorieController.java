package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.service.CategorieService;
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
@RequestMapping("/stock/categories")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categorieService.findAll());
        return "stock/categories/list";
    }

    @GetMapping("/new")
    public String newCategorie(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "stock/categories/form";
    }

    @GetMapping("/edit/{id}")
    public String editCategorie(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("categorie", categorieService.findById(id));
            return "stock/categories/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Catégorie non trouvée");
            return "redirect:/stock/categories";
        }
    }

    @GetMapping("/{id}")
    public String viewCategorie(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("categorie", categorieService.findById(id));
            return "stock/categories/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Catégorie non trouvée");
            return "redirect:/stock/categories";
        }
    }

    @PostMapping("/save")
    public String saveCategorie(@Valid @ModelAttribute Categorie categorie,
                                BindingResult result,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "stock/categories/form";
        }

        try {
            categorieService.save(categorie, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Catégorie enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
        return "redirect:/stock/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategorie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categorieService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Catégorie supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/stock/categories";
    }
}
