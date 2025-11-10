package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {
    Optional<Produit> findByReference(String reference);

    @Query("SELECT p FROM Produit p WHERE p.quantite <= p.seuilAlerte")
    List<Produit> findProduitsEnRupture();

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.quantite <= p.seuilAlerte")
    Long countProduitsEnRupture();

    @Query("SELECT SUM(p.quantite * p.prix) FROM Produit p")
    Double getTotalStockValue();
}