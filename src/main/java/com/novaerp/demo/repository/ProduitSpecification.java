package com.novaerp.demo.repository;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProduitSpecification {

    public static Specification<Produit> withCriteria(ProduitSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getReference() != null && !criteria.getReference().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("reference")),
                        "%" + criteria.getReference().toLowerCase() + "%"));
            }

            if (criteria.getNom() != null && !criteria.getNom().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nom")),
                        "%" + criteria.getNom().toLowerCase() + "%"));
            }

            if (criteria.getCategorieId() != null) {
                predicates.add(cb.equal(root.get("categorie").get("id"), criteria.getCategorieId()));
            }

            if (criteria.getFournisseurId() != null) {
                predicates.add(cb.equal(root.get("fournisseur").get("id"), criteria.getFournisseurId()));
            }

            if (criteria.getPrixMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("prixVente"), criteria.getPrixMin()));
            }

            if (criteria.getPrixMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("prixVente"), criteria.getPrixMax()));
            }

            if (criteria.getQuantiteMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("quantite"), criteria.getQuantiteMin()));
            }

            if (criteria.getQuantiteMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantite"), criteria.getQuantiteMax()));
            }

            if (criteria.getEnRupture() != null && criteria.getEnRupture()) {
                predicates.add(cb.and(
                        cb.isNotNull(root.get("seuilAlerte")),
                        cb.le(root.get("quantite"), root.get("seuilAlerte"))
                ));
            }

            if (criteria.getUnite() != null && !criteria.getUnite().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("unite")),
                        "%" + criteria.getUnite().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
