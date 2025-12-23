package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Categorie;

import java.util.List;

public interface CategorieService {
    List<Categorie> findAll();
    Categorie findById(Long id);
    Categorie save(Categorie categorie, String currentUserEmail);
    void deleteById(Long id);
    List<Categorie> findByCreatedById(Long userId);
}
