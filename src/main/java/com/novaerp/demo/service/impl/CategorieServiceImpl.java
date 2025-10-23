package com.novaerp.demo.service.imp;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.repository.CategorieRepository;
import com.novaerp.demo.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public Categorie create(Categorie categorie) {
        if (categorieRepository.existsByNom(categorie.getNom())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        return categorieRepository.save(categorie);
    }

    @Override
    public Categorie update(Long id, Categorie categorie) {
        Categorie existing = findById(id);
        existing.setCode(categorie.getCode());
        existing.setNom(categorie.getNom());
        existing.setDescription(categorie.getDescription());
        existing.setOrdreAffichage(categorie.getOrdreAffichage());
        return categorieRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Categorie categorie = findById(id);
        categorieRepository.delete(categorie);
    }
}