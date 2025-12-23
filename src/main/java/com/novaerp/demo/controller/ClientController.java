package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.ClientSearchCriteria;
import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/sales/clients")
@PreAuthorize("hasRole('VENTE')")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String listClients(@ModelAttribute("searchCriteria") ClientSearchCriteria criteria, Model model) {
        List<Client> clients;

        // Vérifier si au moins un critère de recherche est renseigné
        boolean hasSearchCriteria = (criteria.getCode() != null && !criteria.getCode().trim().isEmpty()) ||
                (criteria.getNom() != null && !criteria.getNom().trim().isEmpty()) ||
                (criteria.getEmail() != null && !criteria.getEmail().trim().isEmpty()) ||
                (criteria.getTelephone() != null && !criteria.getTelephone().trim().isEmpty()) ||
                (criteria.getAdresse() != null && !criteria.getAdresse().trim().isEmpty());

        if (hasSearchCriteria) {
            clients = clientService.search(criteria);
        } else {
            clients = clientService.findAll();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("searchCriteria", criteria);
        return "sales/clients/list";
    }

    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        return "sales/clients/form";
    }

    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("client", clientService.findById(id));
            return "sales/clients/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Client non trouvé");
            return "redirect:/sales/clients";
        }
    }

    @PostMapping("/save")
    public String saveClient(@Valid @ModelAttribute Client client,
                             BindingResult result,
                             Authentication auth,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("client", client);
            return "sales/clients/form";
        }

        try {
            clientService.save(client, auth.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Client enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
        return "redirect:/sales/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Client supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/sales/clients";
    }
}
