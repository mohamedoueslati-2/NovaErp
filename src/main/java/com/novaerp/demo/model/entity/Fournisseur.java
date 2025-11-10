package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le code du fournisseur ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull(message = "Le nom du fournisseur ne peut pas être nul.")
    @Column(nullable = false)
    private String nom;

    private String adresse;

    private String telephone;

    @NotNull(message = "L'email du fournisseur ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "fournisseur")
    private List<Produit> produits;

    @OneToMany(mappedBy = "fournisseur")
    private List<CommandeFournisseur> commandes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}