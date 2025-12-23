package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.repository.ProduitSpecification;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final UserRepository userRepository;

    @Override
    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    @Override
    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID : " + id));
    }

    @Override
    @Transactional
    public Produit save(Produit produit, String currentUserEmail) {
        if (produit.getId() == null) {
            User createdBy = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            produit.setCreatedBy(createdBy);
            if (produit.getQuantite() == null) {
                produit.setQuantite(0);
            }
            return produitRepository.save(produit);
        } else {
            User updatedBy = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Produit existing = produitRepository.findById(produit.getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            existing.setReference(produit.getReference());
            existing.setNom(produit.getNom());
            existing.setDescription(produit.getDescription());
            existing.setPrixAchatParDefaut(produit.getPrixAchatParDefaut());
            existing.setPrixVente(produit.getPrixVente());
            existing.setQuantite(produit.getQuantite());
            existing.setSeuilAlerte(produit.getSeuilAlerte());
            existing.setUnite(produit.getUnite());
            existing.setImageUrl(produit.getImageUrl());
            existing.setCategorie(produit.getCategorie());
            existing.setFournisseur(produit.getFournisseur());

            existing.setUpdatedBy(updatedBy);
            return produitRepository.save(existing);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID : " + id);
        }
        produitRepository.deleteById(id);
    }

    @Override
    public Optional<Produit> findByReference(String reference) {
        return produitRepository.findByReference(reference);
    }

    @Override
    public Optional<Produit> findByNom(String nom) {
        return produitRepository.findByNom(nom);
    }

    @Override
    public List<Produit> findByCategorieId(Long categorieId) {
        return produitRepository.findByCategorieId(categorieId);
    }

    @Override
    public List<Produit> findByFournisseurId(Long fournisseurId) {
        return produitRepository.findByFournisseurId(fournisseurId);
    }

    @Override
    public List<Produit> findByCreatedById(Long userId) {
        return produitRepository.findByCreatedById(userId);
    }

    @Override
    @Transactional
    public Produit adjustStock(Long produitId, int delta, String currentUserEmail) {
        User updatedBy = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        int current = produit.getQuantite() == null ? 0 : produit.getQuantite();
        int updated = current + delta;
        if (updated < 0) {
            throw new RuntimeException("Stock insuffisant pour décrémenter de " + delta);
        }
        produit.setQuantite(updated);
        produit.setUpdatedBy(updatedBy);
        return produitRepository.save(produit);
    }

    @Override
    public List<Produit> findByCriteria(ProduitSearchCriteria criteria) {
        if (criteria == null) {
            return findAll();
        }
        return produitRepository.findAll(ProduitSpecification.withCriteria(criteria));
    }
}
