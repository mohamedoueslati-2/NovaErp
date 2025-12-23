package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.service.RoleService;
import com.novaerp.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            userService.save(user);
            return ResponseEntity.ok("Utilisateur créé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création : " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            userService.update(id, user);
            return ResponseEntity.ok("Utilisateur modifié avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCategoriesByUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getCategoriesByUser(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des catégories : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/fournisseurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFournisseursByUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getFournisseursByUser(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des fournisseurs : " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> viewProfile(Authentication auth) {
        try {
            User user = userService.findByEmail(auth.getName());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors du chargement du profil");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody User user, Authentication auth) {
        try {
            User currentUser = userService.findByEmail(auth.getName());
            user.setRoles(currentUser.getRoles());
            userService.updateProfile(auth.getName(), user);
            return ResponseEntity.ok("Profil mis à jour avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
}
