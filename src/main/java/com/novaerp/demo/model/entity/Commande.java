package com.novaerp.demo.model.entity;

import com.novaerp.demo.model.typologie.CommandeEtat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateCommande;

    @NotNull(message = "L'état de la commande ne peut pas être nul.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandeEtat etat = CommandeEtat.EN_COURS;  // Valeur par défaut

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande;

    @OneToOne(mappedBy = "commande")
    private Facture facture;
}