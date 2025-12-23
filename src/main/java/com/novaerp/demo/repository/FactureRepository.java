package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long>, JpaSpecificationExecutor<Facture> {
    List<Facture> findByStatut(String statut);
    List<Facture> findByDateFactureBetween(LocalDate debut, LocalDate fin);
    List<Facture> findByCommandeId(Long commandeId);

    // Ajouts pour séparation par type
    List<Facture> findByStatutAndType(String statut, FactureType type);
    List<Facture> findAllByType(FactureType type);
}