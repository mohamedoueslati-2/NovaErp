package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ligne_commande_achat")
public class LigneCommandeAchat extends LigneCommande {

    @NotNull
    @Column(name = "prix_achat", nullable = false)
    private Double prixAchat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_achat_id")
    private CommandeAchat commandeAchat;

    public Double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(Double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public CommandeAchat getCommandeAchat() {
        return commandeAchat;
    }

    public void setCommandeAchat(CommandeAchat commandeAchat) {
        this.commandeAchat = commandeAchat;
    }
}
