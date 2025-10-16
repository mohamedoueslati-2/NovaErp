package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "Le nom du fournisseur ne peut pas être nul.")
    @Column(nullable = false)
    private String nom;

    private String adresse;

    private String telephone;

    @NotNull(message = "L'email du fournisseur ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false) // Clé étrangère vers User
    private User createdBy;

    @OneToMany(mappedBy = "fournisseur")
    private List<Produit> produits;

    @OneToMany(mappedBy = "fournisseur")
    private List<CommandeFournisseur> commandes;

}