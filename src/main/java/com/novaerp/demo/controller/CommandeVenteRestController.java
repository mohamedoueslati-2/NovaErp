package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.CommandeVenteSearchCriteria;
import com.novaerp.demo.model.entity.CommandeVente;
import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.repository.ClientRepository;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.service.CommandeVenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/commandes-vente")
@PreAuthorize("hasRole('VENTE')")
public class CommandeVenteRestController {

    @Autowired
    private CommandeVenteService commandeService;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @GetMapping
    public ResponseEntity<List<CommandeVente>> list(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) CommandeStatut statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long factureId) {

        CommandeVenteSearchCriteria criteria = new CommandeVenteSearchCriteria();
        criteria.setClientId(clientId);
        criteria.setStatut(statut);
        criteria.setDateDebut(dateDebut);
        criteria.setDateFin(dateFin);
        criteria.setFactureId(factureId);

        List<CommandeVente> list = commandeService.search(criteria);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> getClients() {
        return ResponseEntity.ok(clientRepo.findAll());
    }

    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getProduits() {
        return ResponseEntity.ok(produitRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam Long clientId) {
        try {
            CommandeVente commande = commandeService.creerCommande(clientId);
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création : " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        CommandeVente commande = commandeService.findById(id);
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
            @RequestParam Integer quantite) {
        try {
            commandeService.modifierLigne(commandeId, ligneId, quantite);
            return ResponseEntity.ok("Ligne modifiée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/add-ligne")
    public ResponseEntity<?> addLigne(
            @PathVariable Long id,
            @RequestParam Long produitId,
            @RequestParam Integer quantite) {
        try {
            commandeService.ajouterLigne(id, produitId, quantite);
            return ResponseEntity.ok("Ligne ajoutée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<?> terminer(@PathVariable Long id) {
        try {
            CommandeVente commande = commandeService.terminerCommande(id);
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
