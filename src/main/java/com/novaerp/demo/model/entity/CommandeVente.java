package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande_vente")
public class CommandeVente extends Commande {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "commandeVente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommandeVente> lignes = new ArrayList<>();

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<LigneCommandeVente> getLignes() {
        return lignes;
    }

    public void addLigne(LigneCommandeVente ligne) {
        lignes.add(ligne);
        ligne.setCommandeVente(this);
        recalcTotal();
    }

    public void removeLigne(LigneCommandeVente ligne) {
        lignes.remove(ligne);
        ligne.setCommandeVente(null);
        recalcTotal();
    }

    private void recalcTotal() {
        this.total = lignes.stream()
                .map(l -> l.getPrixVente().multiply(BigDecimal.valueOf(l.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
