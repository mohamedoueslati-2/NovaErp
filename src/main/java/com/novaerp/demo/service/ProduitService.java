package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;

import java.util.List;
import java.util.Optional;

public interface ProduitService {
    List<Produit> findAll();
    Produit findById(Long id);
    Produit save(Produit produit, String currentUserEmail);
    void deleteById(Long id);

    Optional<Produit> findByReference(String reference);
    Optional<Produit> findByNom(String nom);
    List<Produit> findByCategorieId(Long categorieId);
    List<Produit> findByFournisseurId(Long fournisseurId);
    List<Produit> findByCreatedById(Long userId);

    Produit adjustStock(Long produitId, int delta, String currentUserEmail);

    List<Produit> findByCriteria(ProduitSearchCriteria criteria);
}
