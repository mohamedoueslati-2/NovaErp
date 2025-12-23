package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {

    Optional<Produit> findByReference(String reference);
    Optional<Produit> findByNom(String nom);
    List<Produit> findByCategorieId(Long categorieId);
    List<Produit> findByFournisseurId(Long fournisseurId);
    List<Produit> findByCreatedById(Long userId);
}
