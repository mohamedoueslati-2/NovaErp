package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.ProduitService;
import com.novaerp.demo.specification.ProduitSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Produit> findAll(Pageable pageable) {
        return produitRepository.findAll(pageable);
    }

    @Override
    public Page<Produit> findWithFilters(ProduitSearchCriteria criteria, Pageable pageable) {
        Sort sort = Sort.by(
                criteria.getSortDirection().equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                criteria.getSortBy()
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        return produitRepository.findAll(
                ProduitSpecification.withFilters(criteria),
                sortedPageable
        );
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
    public Map<String, Object> getStockStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProduits", produitRepository.count());
        stats.put("produitsEnRupture", produitRepository.countProduitsEnRupture());
        stats.put("valeurStock", produitRepository.getTotalStockValue());
        return stats;
    }
}