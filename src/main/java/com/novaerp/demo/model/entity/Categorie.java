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
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le code de la catégorie ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull(message = "Le nom de la catégorie ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    private Integer ordreAffichage;

    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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