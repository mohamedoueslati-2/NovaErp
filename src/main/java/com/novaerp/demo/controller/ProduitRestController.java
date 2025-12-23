package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.service.CategorieService;
import com.novaerp.demo.service.FournisseurService;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class ProduitRestController {

    private final ProduitService produitService;
    private final CategorieService categorieService;
    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<List<Produit>> listProduits(@ModelAttribute ProduitSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new ProduitSearchCriteria();
        }
        return ResponseEntity.ok(produitService.findByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduit(@PathVariable Long id) {
        Produit produit = produitService.findById(id);
        if (produit == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produit);
    }

    @PostMapping
    public ResponseEntity<?> saveProduit(@Valid @RequestBody Produit produit, Authentication auth) {
        try {
            produitService.save(produit, auth.getName());
            return ResponseEntity.ok("Produit enregistré avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("imageFile") MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier envoyé");
        }
        try {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String original = imageFile.getOriginalFilename();
            String filename = System.currentTimeMillis() + "-" + original;
            Path target = uploadDir.resolve(filename).normalize();

            Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Produit produit = produitService.findById(id);
            if (produit == null) {
                return ResponseEntity.notFound().build();
            }
            produit.setImageUrl("/uploads/" + filename);
            produitService.save(produit, null);
            return ResponseEntity.ok("Image uploadée avec succès");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur upload image : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduit(@PathVariable Long id) {
        try {
            produitService.deleteById(id);
            return ResponseEntity.ok("Produit supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<?> adjustStock(@PathVariable Long id,
                                         @RequestParam("delta") int delta,
                                         Authentication auth) {
        try {
            produitService.adjustStock(id, delta, auth.getName());
            return ResponseEntity.ok("Stock ajusté");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur ajustement stock : " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(categorieService.findAll());
    }

    @GetMapping("/fournisseurs")
    public ResponseEntity<?> getFournisseurs() {
        return ResponseEntity.ok(fournisseurService.findAll());
    }
}
