package com.novaerp.demo.controller;

import com.novaerp.demo.model.dto.ClientSearchCriteria;
import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sales/clients")
@PreAuthorize("hasRole('VENTE')")
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

    @GetMapping
    public List<Client> listClients(@ModelAttribute ClientSearchCriteria criteria) {
        boolean hasSearchCriteria = (criteria.getCode() != null && !criteria.getCode().trim().isEmpty()) ||
                (criteria.getNom() != null && !criteria.getNom().trim().isEmpty()) ||
                (criteria.getEmail() != null && !criteria.getEmail().trim().isEmpty()) ||
                (criteria.getTelephone() != null && !criteria.getTelephone().trim().isEmpty()) ||
                (criteria.getAdresse() != null && !criteria.getAdresse().trim().isEmpty());

        if (hasSearchCriteria) {
            return clientService.search(criteria);
        } else {
            return clientService.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClient(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clientService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Client non trouvé");
        }
    }

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody Client client, Authentication auth) {
        try {
            Client saved = clientService.save(client, auth.getName());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @Valid @RequestBody Client client, Authentication auth) {
        try {
            client.setId(id);
            Client updated = clientService.save(client, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteById(id);
            return ResponseEntity.ok("Client supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
