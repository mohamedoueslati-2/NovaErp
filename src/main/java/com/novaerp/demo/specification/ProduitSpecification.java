package com.novaerp.demo.specification;

import com.novaerp.demo.model.dto.ProduitSearchCriteria;
import com.novaerp.demo.model.entity.Produit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProduitSpecification {

    public static Specification<Produit> withFilters(ProduitSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
                String pattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("nom")), pattern);
                Predicate refPredicate = cb.like(cb.lower(root.get("reference")), pattern);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(namePredicate, refPredicate, descPredicate));
            }

            if (criteria.getCategorieId() != null) {
                predicates.add(cb.equal(root.get("categorie").get("id"), criteria.getCategorieId()));
            }

            if (criteria.getFournisseurId() != null) {
                predicates.add(cb.equal(root.get("fournisseur").get("id"), criteria.getFournisseurId()));
            }

            if (criteria.getPrixMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("prix"), criteria.getPrixMin()));
            }

            if (criteria.getPrixMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("prix"), criteria.getPrixMax()));
            }

            if (criteria.getQuantiteMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("quantite"), criteria.getQuantiteMin()));
            }

            if (criteria.getQuantiteMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantite"), criteria.getQuantiteMax()));
            }

            if (criteria.getUnite() != null && !criteria.getUnite().isEmpty()) {
                predicates.add(cb.equal(root.get("unite"), criteria.getUnite()));
            }

            if (criteria.getEnRupture() != null && criteria.getEnRupture()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantite"), root.get("seuilAlerte")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}