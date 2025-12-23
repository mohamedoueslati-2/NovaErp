package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Categorie extends BaseEntity {

    @NotNull(message = "Le code de la catégorie ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull(message = "Le nom de la catégorie ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    private Integer ordreAffichage;
}
