package com.novaerp.demo.service.imp;

import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    @Override
    public Produit create(Produit produit) {
        if (produitRepository.findByReference(produit.getReference()).isPresent()) {
            throw new RuntimeException("Un produit avec cette référence existe déjà");
        }
        return produitRepository.save(produit);
    }

    @Override
    public Produit update(Long id, Produit produit) {
        Produit existing = findById(id);
        existing.setReference(produit.getReference());
        existing.setNom(produit.getNom());
        existing.setDescription(produit.getDescription());
        existing.setPrix(produit.getPrix());
        existing.setQuantite(produit.getQuantite());
        existing.setSeuilAlerte(produit.getSeuilAlerte());
        existing.setUnite(produit.getUnite());
        existing.setImageUrl(produit.getImageUrl());
        existing.setCategorie(produit.getCategorie());
        existing.setFournisseur(produit.getFournisseur());
        return produitRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Produit produit = findById(id);
        produitRepository.delete(produit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findByCategorie(Long categorieId) {
        return produitRepository.findByCategorie_Id(categorieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findByFournisseur(Long fournisseurId) {
        return produitRepository.findByFournisseur_Id(fournisseurId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produit> findProduitsEnRuptureStock() {
        return produitRepository.findProduitsEnRuptureStock();
    }
}