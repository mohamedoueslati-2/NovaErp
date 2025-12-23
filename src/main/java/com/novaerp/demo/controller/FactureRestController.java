package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.FactureSearchCriteria;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures")
@PreAuthorize("hasRole('STOCK')")
@RequiredArgsConstructor
public class FactureRestController {

    private final FactureService factureService;

    @GetMapping
    public ResponseEntity<?> listFactures(
            @ModelAttribute FactureSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (criteria.getType() == null) {
            criteria.setType(FactureType.FACTURE_FOURNISSEUR);
        }
        Page<Facture> facturePage = factureService.findWithFilters(criteria, PageRequest.of(page, size));
        return ResponseEntity.ok(facturePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailFacture(@PathVariable Long id) {
        Facture facture = factureService.findById(id);
        if (facture == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facture);
    }

    @PostMapping
    public ResponseEntity<?> saveFacture(@Valid @RequestBody Facture facture) {
        if (facture.getType() == null) {
            facture.setType(FactureType.FACTURE_FOURNISSEUR);
        }
        factureService.save(facture);
        return ResponseEntity.ok("Facture enregistrée avec succès");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFacture(@PathVariable Long id) {
        try {
            factureService.deleteById(id);
            return ResponseEntity.ok("Facture supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression");
        }
    }

    @GetMapping("/non-payees")
    public ResponseEntity<?> facturesNonPayees() {
        return ResponseEntity.ok(factureService.findFacturesNonPayees(FactureType.FACTURE_FOURNISSEUR));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        return ResponseEntity.ok(factureService.getFactureStatistics(FactureType.FACTURE_FOURNISSEUR));
    }
}
