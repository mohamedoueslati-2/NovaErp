package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable Long id) {
        Role role = roleService.findById(id);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(role);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getRoleTypes() {
        return ResponseEntity.ok(Arrays.asList(RoleTypologie.values()));
    }

    @PostMapping
    public ResponseEntity<?> saveRole(@Valid @RequestBody Role role) {
        try {
            roleService.save(role);
            String message = (role.getId() == null) ? "Rôle créé avec succès" : "Rôle modifié avec succès";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'enregistrement du rôle : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.ok("Le rôle a été supprimé avec succès.");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.badRequest().body("Impossible de supprimer : Rôle non trouvé pour l'ID " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression du rôle : " + e.getMessage());
        }
    }
}
