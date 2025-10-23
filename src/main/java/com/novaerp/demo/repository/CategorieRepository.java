package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByCode(String code);

    Optional<Categorie> findByNom(String nom);

    List<Categorie> findAllByOrderByOrdreAffichageAsc();
}