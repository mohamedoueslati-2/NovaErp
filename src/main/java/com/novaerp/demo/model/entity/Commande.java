package com.novaerp.demo.model.entity;

import com.novaerp.demo.model.typologie.CommandeStatut;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "commande")
public abstract class Commande extends BaseEntity {

    @Column(nullable = false)
    private LocalDate dateCommande = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandeStatut statut = CommandeStatut.EN_COURS;

    @Column(name = "facture_id")
    private Long factureId;

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public CommandeStatut getStatut() {
        return statut;
    }

    public void setStatut(CommandeStatut statut) {
        this.statut = statut;
    }

    public Long getFactureId() {
        return factureId;
    }

    public void setFactureId(Long factureId) {
        this.factureId = factureId;
    }
}
