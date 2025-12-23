package com.novaerp.demo.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitSearchCriteria {
    private String reference;
    private String nom;
    private Long categorieId;
    private Long fournisseurId;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private Integer quantiteMin;
    private Integer quantiteMax;
    private String unite;
    private Boolean enRupture;
}
