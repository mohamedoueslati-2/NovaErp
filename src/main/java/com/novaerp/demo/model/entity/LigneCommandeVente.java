package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_commande_vente")
public class LigneCommandeVente extends LigneCommande {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_vente_id")
    private CommandeVente commandeVente;

    @NotNull
    @Column(name = "prix_vente", precision = 15, scale = 4, nullable = false)
    private BigDecimal prixVente;

    public CommandeVente getCommandeVente() {
        return commandeVente;
    }

    public void setCommandeVente(CommandeVente commandeVente) {
        this.commandeVente = commandeVente;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }
}
