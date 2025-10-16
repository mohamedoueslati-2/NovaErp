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
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom de la catégorie ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String nom;

    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;


}
