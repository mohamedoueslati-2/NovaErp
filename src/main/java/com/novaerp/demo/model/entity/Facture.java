package com.novaerp.demo.model.entity;

import com.novaerp.demo.model.typologie.FactureType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date de la facture ne peut pas être nulle.")
    private LocalDate dateFacture;

    @NotNull(message = "Le montant total ne peut pas être nul.")
    private Double montantTotal;

    @NotNull(message = "Le type de facture ne peut pas être nul.")
    @Enumerated(EnumType.STRING)
    private FactureType type;

    @OneToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
}