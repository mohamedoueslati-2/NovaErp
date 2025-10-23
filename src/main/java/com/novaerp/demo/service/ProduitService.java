package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Produit;

import java.util.List;

public interface ProduitService {
    List<Produit> findAll();
    Produit findById(Long id);
    Produit save(Produit produit, String currentUserEmail);
    void deleteById(Long id);
    List<Produit> findProduitsEnRupture();
    List<Produit> searchByKeyword(String keyword);
}