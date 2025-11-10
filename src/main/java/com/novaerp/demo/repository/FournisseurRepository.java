package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findByCode(String code);

    Optional<Fournisseur> findByEmail(String email);
}