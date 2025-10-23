package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Produit;

import java.util.List;

public interface ProduitService {
    Produit create(Produit produit);
    Produit update(Long id, Produit produit);
    Produit findById(Long id);
    List<Produit> findAll();
    void delete(Long id);
    List<Produit> findByCategorie(Long categorieId);
    List<Produit> findByFournisseur(Long fournisseurId);
    List<Produit> findProduitsEnRuptureStock();
}