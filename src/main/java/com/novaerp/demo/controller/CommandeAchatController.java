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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/stock/commandes/achat")
@PreAuthorize("hasRole('STOCK')")

public class CommandeAchatController {

    @Autowired
    private CommandeAchatService commandeService;

    @Autowired
    private FournisseurRepository fournisseurRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @GetMapping
    public String list(
            @RequestParam(required = false) Long fournisseurId,
            @RequestParam(required = false) CommandeStatut statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) Long factureId,
            Model model) {

        CommandeAchatSearchCriteria criteria = new CommandeAchatSearchCriteria();
        criteria.setFournisseurId(fournisseurId);
        criteria.setStatut(statut);
        criteria.setDateDebut(dateDebut);
        criteria.setDateFin(dateFin);
        criteria.setFactureId(factureId);

        List<CommandeAchat> list = commandeService.searchCommandes(criteria);
        List<Fournisseur> fournisseurs = fournisseurRepo.findAll();

        model.addAttribute("list", list);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("statuts", CommandeStatut.values());
        model.addAttribute("criteria", criteria);

        return "stock/commandes/achat/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        List<Fournisseur> fournisseurs = fournisseurRepo.findAll();
        model.addAttribute("fournisseurs", fournisseurs);
        return "stock/commandes/achat/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long fournisseurId, RedirectAttributes redirectAttributes) {
        try {
            CommandeAchat commande = commandeService.creerCommande(fournisseurId);
            redirectAttributes.addFlashAttribute("success", "Commande créée avec succès");
            return "redirect:/stock/commandes/achat/" + commande.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/stock/commandes/achat/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CommandeAchat commande = commandeService.findById(id);
        List<Produit> produits = produitRepo.findAll();

        model.addAttribute("commande", commande);
        model.addAttribute("produits", produits);
        model.addAttribute("statuts", CommandeStatut.values());

        return "stock/commandes/achat/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commandeService.supprimerCommande(id);
            redirectAttributes.addFlashAttribute("success", "Commande supprimée avec succès");
            return "redirect:/stock/commandes/achat";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/stock/commandes/achat/" + id;
        }
    }

    @PostMapping("/{commandeId}/ligne/{ligneId}/delete")
    public String deleteLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.supprimerLigne(commandeId, ligneId);
            redirectAttributes.addFlashAttribute("success", "Ligne supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/stock/commandes/achat/" + commandeId;
    }

    @PostMapping("/{commandeId}/ligne/{ligneId}/update")
    public String updateLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite,
            @RequestParam Double prixAchat,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.modifierLigne(commandeId, ligneId, quantite, prixAchat);
            redirectAttributes.addFlashAttribute("success", "Ligne modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/stock/commandes/achat/" + commandeId;
    }

    @PostMapping("/{id}/add-ligne")
    public String addLigne(
            @PathVariable Long id,
            @RequestParam Long produitId,
            @RequestParam Integer quantite,
            @RequestParam Double prixAchat,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.ajouterLigne(id, produitId, quantite, prixAchat);
            redirectAttributes.addFlashAttribute("success", "Ligne ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
        }
        return "redirect:/stock/commandes/achat/" + id;
    }

    @PostMapping("/{id}/terminer")
    public String terminer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            CommandeAchat commande = commandeService.terminerCommande(id);
            redirectAttributes.addFlashAttribute("success",
                    "Commande terminée. Facture n° " + commande.getFactureId() + " générée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la finalisation : " + e.getMessage());
        }
        return "redirect:/stock/commandes/achat/" + id;
    }

    @PostMapping("/{id}/statut")
    public String changerStatut(
            @PathVariable Long id,
            @RequestParam("statut") CommandeStatut statut,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.changerStatut(id, statut);
            redirectAttributes.addFlashAttribute("success", "Statut modifié avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/stock/commandes/achat/" + id;
    }
}
