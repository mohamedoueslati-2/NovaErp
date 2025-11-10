package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProduitService {
    Page<Produit> findAll(Pageable pageable);
    Page<Produit> findWithFilters(ProduitSearchCriteria criteria, Pageable pageable);
    Produit findById(Long id);
    Produit save(Produit produit, String currentUserEmail);
    void deleteById(Long id);
    List<Produit> findProduitsEnRupture();
    Map<String, Object> getStockStatistics();
}