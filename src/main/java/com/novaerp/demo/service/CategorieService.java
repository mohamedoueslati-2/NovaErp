package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Categorie;

import java.util.List;

public interface CategorieService {
    Categorie create(Categorie categorie);
    Categorie update(Long id, Categorie categorie);
    Categorie findById(Long id);
    List<Categorie> findAll();
    void delete(Long id);
}