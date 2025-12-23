package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.FournisseurSearchCriteria;
import com.novaerp.demo.model.entity.Fournisseur;

import java.util.List;

public interface FournisseurService {
    List<Fournisseur> findAll();
    Fournisseur findById(Long id);
    Fournisseur save(Fournisseur fournisseur, String currentUserEmail);
    void deleteById(Long id);
    List<Fournisseur> findByCreatedById(Long userId);
    List<Fournisseur> search(FournisseurSearchCriteria criteria);
}
