// java
package com.novaerp.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Personne extends BaseEntity {

    @NotNull(message = "Le code ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull(message = "Le nom ne peut pas être nul.")
    @Column(nullable = false)
    private String nom;

    private String adresse;

    private String telephone;

    @NotNull(message = "L'email ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String email;
}
