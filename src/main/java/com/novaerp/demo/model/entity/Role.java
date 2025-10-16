package com.novaerp.demo.model.entity;

import com.novaerp.demo.model.typologie.RoleTypologie;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom du rôle ne peut pas être nul.")
    @Enumerated(EnumType.STRING) // Stocke les valeurs de l'énumération comme chaînes
    @Column(unique = true, nullable = false)
    private RoleTypologie name;
}