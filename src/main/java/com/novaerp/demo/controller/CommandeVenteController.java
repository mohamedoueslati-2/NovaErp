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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/sales/commandes")
@PreAuthorize("hasRole('VENTE')")
public class CommandeVenteController {

    @Autowired
    private CommandeVenteService commandeService;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @GetMapping
    public String list(Model model,
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
        List<Client> clients = clientRepo.findAll();

        model.addAttribute("list", list);
        model.addAttribute("clients", clients);
        model.addAttribute("statuts", CommandeStatut.values());
        model.addAttribute("criteria", criteria);

        return "sales/commandes/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        List<Client> clients = clientRepo.findAll();
        model.addAttribute("clients", clients);
        return "sales/commandes/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long clientId, RedirectAttributes redirectAttributes) {
        try {
            CommandeVente commande = commandeService.creerCommande(clientId);
            redirectAttributes.addFlashAttribute("success", "Commande de vente créée avec succès");
            return "redirect:/sales/commandes/" + commande.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/sales/commandes/new";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CommandeVente commande = commandeService.findById(id);
        List<Produit> produits = produitRepo.findAll();

        model.addAttribute("commande", commande);
        model.addAttribute("produits", produits);
        model.addAttribute("statuts", CommandeStatut.values());

        return "sales/commandes/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commandeService.supprimerCommande(id);
            redirectAttributes.addFlashAttribute("success", "Commande supprimée avec succès");
            return "redirect:/sales/commandes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/sales/commandes/" + id;
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
        return "redirect:/sales/commandes/" + commandeId;
    }

    @PostMapping("/{commandeId}/ligne/{ligneId}/update")
    public String updateLigne(
            @PathVariable Long commandeId,
            @PathVariable Long ligneId,
            @RequestParam Integer quantite,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.modifierLigne(commandeId, ligneId, quantite);
            redirectAttributes.addFlashAttribute("success", "Ligne modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/sales/commandes/" + commandeId;
    }

    @PostMapping("/{id}/add-ligne")
    public String addLigne(
            @PathVariable Long id,
            @RequestParam Long produitId,
            @RequestParam Integer quantite,
            RedirectAttributes redirectAttributes) {
        try {
            commandeService.ajouterLigne(id, produitId, quantite);
            redirectAttributes.addFlashAttribute("success", "Ligne ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
        }
        return "redirect:/sales/commandes/" + id;
    }

    @PostMapping("/{id}/terminer")
    public String terminer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            CommandeVente commande = commandeService.terminerCommande(id);
            redirectAttributes.addFlashAttribute("success",
                    "Commande terminée. Facture n° " + commande.getFactureId() + " générée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la finalisation : " + e.getMessage());
        }
        return "redirect:/sales/commandes/" + id;
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
        return "redirect:/sales/commandes/" + id;
    }
}
