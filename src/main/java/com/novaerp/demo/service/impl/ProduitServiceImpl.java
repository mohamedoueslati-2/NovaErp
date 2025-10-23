package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        }
        return produitRepository.save(produit);
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
    public List<Produit> findProduitsEnRupture() {
        return produitRepository.findProduitsEnRupture();
    }

    @Override
    public List<Produit> searchByKeyword(String keyword) {
        return produitRepository.searchByKeyword(keyword);
    }
}