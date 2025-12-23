package com.novaerp.demo.repository;

import com.novaerp.demo.model.dto.CommandeAchatSearchCriteria;
import com.novaerp.demo.model.entity.CommandeAchat;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CommandeAchatSpecification {

    public static Specification<CommandeAchat> withCriteria(CommandeAchatSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getFournisseurId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("fournisseurId"), criteria.getFournisseurId()));
            }

            if (criteria.getStatut() != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), criteria.getStatut()));
            }

            if (criteria.getDateDebut() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateCommande"), criteria.getDateDebut()));
            }

            if (criteria.getDateFin() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateCommande"), criteria.getDateFin()));
            }

            if (criteria.getFactureId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("factureId"), criteria.getFactureId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
