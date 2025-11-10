package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.repository.CategorieRepository;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/stock/produits")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieRepository categorieRepository;
    private final FournisseurRepository fournisseurRepository;

    @GetMapping
    public String listProduits(
            @ModelAttribute ProduitSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Produit> produitPage = produitService.findWithFilters(
                criteria,
                PageRequest.of(page, size)
        );

        model.addAttribute("produits", produitPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produitPage.getTotalPages());
        model.addAttribute("totalItems", produitPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("criteria", criteria);
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("fournisseurs", fournisseurRepository.findAll());
        model.addAttribute("produitsEnRupture", produitService.findProduitsEnRupture());
        model.addAttribute("statistics", produitService.getStockStatistics());

        return "stock/produits/list";
    }

    @GetMapping("/new")
    public String newProduit(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("fournisseurs", fournisseurRepository.findAll());
        return "stock/produits/form";
    }

    @GetMapping("/edit/{id}")
    public String editProduit(@PathVariable Long id, Model model) {
        model.addAttribute("produit", produitService.findById(id));
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("fournisseurs", fournisseurRepository.findAll());
        return "stock/produits/form";
    }

    @PostMapping("/save")
    public String saveProduit(@Valid @ModelAttribute Produit produit,
                              BindingResult result,
                              Authentication auth,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categorieRepository.findAll());
            model.addAttribute("fournisseurs", fournisseurRepository.findAll());
            return "stock/produits/form";
        }

        produitService.save(produit, auth.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Produit enregistré avec succès");
        return "redirect:/stock/produits";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produit supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression");
        }
        return "redirect:/stock/produits";
    }
}