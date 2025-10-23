package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    Optional<Produit> findByReference(String reference);
    List<Produit> findByCategorie_Id(Long categorieId);
    List<Produit> findByFournisseur_Id(Long fournisseurId);
    List<Produit> findByQuantiteLessThanEqual(Integer seuil);

    @Query("SELECT p FROM Produit p WHERE p.quantite <= p.seuilAlerte")
    List<Produit> findProduitsEnRuptureStock();
}