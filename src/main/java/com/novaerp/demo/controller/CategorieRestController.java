package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/stock/categories")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class CategorieRestController {

    private final CategorieService categorieService;

    @GetMapping
    public List<Categorie> listCategories() {
        return categorieService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategorie(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categorieService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Catégorie non trouvée");
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategorie(@Valid @RequestBody Categorie categorie, Authentication auth) {
        try {
            Categorie saved = categorieService.save(categorie, auth.getName());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategorie(@PathVariable Long id, @Valid @RequestBody Categorie categorie, Authentication auth) {
        try {
            categorie.setId(id);
            Categorie updated = categorieService.save(categorie, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategorie(@PathVariable Long id) {
        try {
            categorieService.deleteById(id);
            return ResponseEntity.ok("Catégorie supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
