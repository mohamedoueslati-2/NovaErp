package com.novaerp.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public abstract class LigneCommande extends BaseEntity {

    @NotNull
    @Column(name = "produit_id", nullable = false)
    private Long produitId;

    @NotNull
    @Column(nullable = false)
    private Integer quantite;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}
