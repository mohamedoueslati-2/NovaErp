package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.CategorieRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final UserRepository userRepository;

    @Override
    public List<Categorie> findAll() {
        return categorieRepository.findAllByOrderByOrdreAffichageAsc();
    }

    @Override
    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));
    }

    @Override
    @Transactional
    public Categorie save(Categorie categorie, String currentUserEmail) {
        if (categorie.getId() == null) {
            User createdBy = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            categorie.setCreatedBy(createdBy);
        }
        return categorieRepository.save(categorie);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée avec l'ID : " + id);
        }
        categorieRepository.deleteById(id);
    }
}