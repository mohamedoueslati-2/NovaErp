package com.novaerp.demo.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande_achat")
public class CommandeAchat extends Commande {

    @Column(name = "fournisseur_id", nullable = false)
    private Long fournisseurId;

    @Column(nullable = false)
    private Double total = 0.0;

    @OneToMany(mappedBy = "commandeAchat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommandeAchat> lignes = new ArrayList<>();

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<LigneCommandeAchat> getLignes() {
        return lignes;
    }

    public void addLigne(LigneCommandeAchat ligne) {
        lignes.add(ligne);
        ligne.setCommandeAchat(this);
        recalcTotal();
    }

    public void removeLigne(LigneCommandeAchat ligne) {
        lignes.remove(ligne);
        ligne.setCommandeAchat(null);
        recalcTotal();
    }

    private void recalcTotal() {
        this.total = lignes.stream()
                .mapToDouble(l -> l.getPrixAchat() * l.getQuantite())
                .sum();
    }
}
