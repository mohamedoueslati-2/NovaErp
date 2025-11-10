package com.novaerp.demo.model.dto;

import lombok.Data;

@Data
public class ProduitSearchCriteria {
    private String keyword;
    private Long categorieId;
    private Long fournisseurId;
    private Double prixMin;
    private Double prixMax;
    private Integer quantiteMin;
    private Integer quantiteMax;
    private String unite;
    private Boolean enRupture;
    private String sortBy = "nom";
    private String sortDirection = "asc";
}