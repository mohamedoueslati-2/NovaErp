package com.novaerp.demo.repository;

import com.novaerp.demo.model.dto.FournisseurSearchCriteria;
import com.novaerp.demo.model.entity.Fournisseur;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FournisseurSpecification {

    public static Specification<Fournisseur> withCriteria(FournisseurSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getCode() != null && !criteria.getCode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        "%" + criteria.getCode().toLowerCase().trim() + "%"
                ));
            }

            if (criteria.getNom() != null && !criteria.getNom().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nom")),
                        "%" + criteria.getNom().toLowerCase().trim() + "%"
                ));
            }

            if (criteria.getEmail() != null && !criteria.getEmail().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase().trim() + "%"
                ));
            }

            if (criteria.getTelephone() != null && !criteria.getTelephone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("telephone"),
                        "%" + criteria.getTelephone().trim() + "%"
                ));
            }

            if (criteria.getAdresse() != null && !criteria.getAdresse().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("adresse")),
                        "%" + criteria.getAdresse().toLowerCase().trim() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
