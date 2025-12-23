package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.service.CategorieService;
import com.novaerp.demo.service.FournisseurService;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/stock/produits")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final CategorieService categorieService;
    private final FournisseurService fournisseurService;

    @GetMapping
    public String listProduits(@ModelAttribute("criteria") ProduitSearchCriteria criteria,
                               @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                               Model model) {
        if (criteria == null) {
            criteria = new ProduitSearchCriteria();
        }

        model.addAttribute("produits", produitService.findByCriteria(criteria));
        model.addAttribute("criteria", criteria);
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("fournisseurs", fournisseurService.findAll());

        // Si c'est une requête AJAX, retourner seulement le fragment
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "stock/produits/list :: content";
        }

        return "stock/produits/list";
    }

    @GetMapping("/new")
    public String newProduit(@RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                             Model model) {
        model.addAttribute("produit", new Produit());
        loadRefs(model);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "stock/produits/form :: content";
        }

        return "stock/produits/form";
    }

    @GetMapping("/edit/{id}")
    public String editProduit(@PathVariable Long id,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("produit", produitService.findById(id));
            loadRefs(model);

            if ("XMLHttpRequest".equals(requestedWith)) {
                return "stock/produits/form :: content";
            }

            return "stock/produits/form";
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("errorMessage", "Produit non trouvé");
                return "error :: message";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Produit non trouvé");
            return "redirect:/stock/produits";
        }
    }

    @GetMapping("/{id}")
    public String viewProduit(@PathVariable Long id,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("produit", produitService.findById(id));

            if ("XMLHttpRequest".equals(requestedWith)) {
                return "stock/produits/view :: content";
            }

            return "stock/produits/view";
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("errorMessage", "Produit non trouvé");
                return "error :: message";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Produit non trouvé");
            return "redirect:/stock/produits";
        }
    }

    @PostMapping("/save")
    public String saveProduit(@Valid @ModelAttribute Produit produit,
                              BindingResult result,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                              Authentication auth,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            loadRefs(model);
            if ("XMLHttpRequest".equals(requestedWith)) {
                return "stock/produits/form :: content";
            }
            return "stock/produits/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadDir = Paths.get("uploads");
                Files.createDirectories(uploadDir);

                String original = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String filename = System.currentTimeMillis() + "-" + original;
                Path target = uploadDir.resolve(filename).normalize();

                Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                produit.setImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur upload image : " + e.getMessage());
                return "redirect:/stock/produits";
            }
        }

        try {
            produitService.save(produit, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Produit enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/stock/produits";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduit(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            produitService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produit supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/stock/produits";
    }

    @PostMapping("/{id}/adjust-stock")
    public String adjustStock(@PathVariable Long id,
                              @RequestParam("delta") int delta,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            produitService.adjustStock(id, delta, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Stock ajusté");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur ajustement stock : " + e.getMessage());
        }
        return "redirect:/stock/produits";
    }

    private void loadRefs(Model model) {
        model.addAttribute("categories", categorieService.findAll());
        model.addAttribute("fournisseurs", fournisseurService.findAll());
    }
}
