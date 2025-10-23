package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Fournisseur;

import java.util.List;

public interface FournisseurService {
    Fournisseur create(Fournisseur fournisseur);
    Fournisseur update(Long id, Fournisseur fournisseur);
    Fournisseur findById(Long id);
    List<Fournisseur> findAll();
    void delete(Long id);
}