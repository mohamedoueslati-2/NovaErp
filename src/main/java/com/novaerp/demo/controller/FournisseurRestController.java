package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.FournisseurSearchCriteria;
import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class FournisseurRestController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<List<Fournisseur>> listFournisseurs(@ModelAttribute FournisseurSearchCriteria criteria) {
        List<Fournisseur> fournisseurs = fournisseurService.search(criteria);
        return ResponseEntity.ok(fournisseurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFournisseur(@PathVariable Long id) {
        Fournisseur fournisseur = fournisseurService.findById(id);
        if (fournisseur == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fournisseur);
    }

    @PostMapping
    public ResponseEntity<?> saveFournisseur(@Valid @RequestBody Fournisseur fournisseur, Authentication auth) {
        try {
            fournisseurService.save(fournisseur, auth.getName());
            return ResponseEntity.ok("Fournisseur enregistré avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFournisseur(@PathVariable Long id) {
        try {
            fournisseurService.deleteById(id);
            return ResponseEntity.ok("Fournisseur supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
