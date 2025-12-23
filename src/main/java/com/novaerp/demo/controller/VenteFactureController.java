package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.FactureSearchCriteria;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales/factures")
@PreAuthorize("hasRole('VENTE')")
@RequiredArgsConstructor
public class VenteFactureController {

    private final FactureService factureService;

    @GetMapping
    public String listFactures(@ModelAttribute FactureSearchCriteria criteria,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        // Définir le type par défaut pour les factures clients
        if (criteria.getType() == null) {
            criteria.setType(FactureType.FACTURE_CLIENT);
        }

        Page<Facture> facturePage = factureService.findWithFilters(criteria, PageRequest.of(page, size));

        model.addAttribute("factures", facturePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", facturePage.getTotalPages());
        model.addAttribute("totalItems", facturePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("criteria", criteria);
        model.addAttribute("facturesNonPayees", factureService.findFacturesNonPayees(FactureType.FACTURE_CLIENT));
        model.addAttribute("statistics", factureService.getFactureStatistics(FactureType.FACTURE_CLIENT));
        model.addAttribute("basePath", "/sales/factures");

        return "sales/factures/list";
    }

    @GetMapping("/new")
    public String newFacture(Model model) {
        Facture f = new Facture();
        f.setType(FactureType.FACTURE_CLIENT);
        model.addAttribute("facture", f);
        model.addAttribute("basePath", "/sales/factures");
        return "sales/factures/form";
    }

    @GetMapping("/edit/{id}")
    public String editFacture(@PathVariable Long id, Model model) {
        model.addAttribute("facture", factureService.findById(id));
        model.addAttribute("basePath", "/sales/factures");
        return "sales/factures/form";
    }

    @PostMapping("/save")
    public String saveFacture(@Valid @ModelAttribute Facture facture,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("basePath", "/sales/factures");
            return "sales/factures/form";
        }
        if (facture.getType() == null) {
            facture.setType(FactureType.FACTURE_CLIENT);
        }
        factureService.save(facture);
        redirectAttributes.addFlashAttribute("successMessage", "Facture enregistrée avec succès");
        return "redirect:/sales/factures";
    }

    @PostMapping("/delete/{id}")
    public String deleteFacture(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            factureService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Facture supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression");
        }
        return "redirect:/sales/factures";
    }

    @GetMapping("/detail/{id}")
    public String detailFacture(@PathVariable Long id, Model model) {
        model.addAttribute("facture", factureService.findById(id));
        model.addAttribute("basePath", "/sales/factures");
        return "sales/factures/detail";
    }
}
