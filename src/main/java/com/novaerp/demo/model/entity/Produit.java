package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Produit extends BaseEntity {

    @NotNull(message = "La référence du produit ne peut pas être nulle.")
    @Column(unique = true, nullable = false)
    private String reference;

    @NotNull(message = "Le nom du produit ne peut pas être nul.")
    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(precision = 15, scale = 4)
    private BigDecimal prixAchatParDefaut;

    @Column(precision = 15, scale = 4)
    private BigDecimal prixVente;

    private Integer quantite;

    private Integer seuilAlerte;

    private String unite;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    private Fournisseur fournisseur;

}
