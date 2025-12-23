package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.CommandeAchatSearchCriteria;
import com.novaerp.demo.model.entity.CommandeAchat;
import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.service.CommandeAchatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/commandes-achat")
@PreAuthorize("hasRole('STOCK')")
public class CommandeAchatRestController {

    @Autowired
    private CommandeAchatService commandeService;

    @Autowired
    private FournisseurRepository fournisseurRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @GetMapping
    public ResponseEntity<List<CommandeAchat>> list(
            @RequestParam(required = false) Long fournisseurId,
            @RequestParam(required = false) CommandeStatut statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long factureId) {

        CommandeAchatSearchCriteria criteria = new CommandeAchatSearchCriteria();
        criteria.setFournisseurId(fournisseurId);
        criteria.setStatut(statut);
        criteria.setDateDebut(dateDebut);
        criteria.setDateFin(dateFin);
        criteria.setFactureId(factureId);

        List<CommandeAchat> list = commandeService.searchCommandes(criteria);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/fournisseurs")
    public ResponseEntity<List<Fournisseur>> getFournisseurs() {
        return ResponseEntity.ok(fournisseurRepo.findAll());
    }

    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getProduits() {
        return ResponseEntity.ok(produitRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam Long fournisseurId) {
        try {
            CommandeAchat commande = commandeService.creerCommande(fournisseurId);
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création : " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        CommandeAchat commande = commandeService.findById(id);
        if (commande == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            commandeService.supprimerCommande(id);
            return ResponseEntity.ok("Commande supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @DeleteMapping("/{commandeId}/ligne/{ligneId}")
    public ResponseEntity<?> deleteLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId) {
        try {
            commandeService.supprimerLigne(commandeId, ligneId);
            return ResponseEntity.ok("Ligne supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PutMapping("/{commandeId}/ligne/{ligneId}")
    public ResponseEntity<?> updateLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite,
            @RequestParam Double prixAchat) {
        try {
            commandeService.modifierLigne(commandeId, ligneId, quantite, prixAchat);
            return ResponseEntity.ok("Ligne modifiée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/add-ligne")
    public ResponseEntity<?> addLigne(
            @PathVariable Long id,
            @RequestParam Long produitId,
            @RequestParam Integer quantite,
            @RequestParam Double prixAchat) {
        try {
            commandeService.ajouterLigne(id, produitId, quantite, prixAchat);
            return ResponseEntity.ok("Ligne ajoutée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<?> terminer(@PathVariable Long id) {
        try {
            CommandeAchat commande = commandeService.terminerCommande(id);
            return ResponseEntity.ok("Commande terminée. Facture n° " + commande.getFactureId() + " générée.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la finalisation : " + e.getMessage());
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> changerStatut(
            @PathVariable Long id,
            @RequestParam("statut") CommandeStatut statut) {
        try {
            commandeService.changerStatut(id, statut);
            return ResponseEntity.ok("Statut modifié avec succès.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
}
