package com.novaerp.demo.repository;

import com.novaerp.demo.model.dto.CommandeVenteSearchCriteria;
import com.novaerp.demo.model.entity.CommandeVente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CommandeVenteSpecification {

    public static Specification<CommandeVente> withCriteria(CommandeVenteSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getClientId() != null) {
                predicates.add(cb.equal(root.get("clientId"), criteria.getClientId()));
            }

            if (criteria.getStatut() != null) {
                predicates.add(cb.equal(root.get("statut"), criteria.getStatut()));
            }

            if (criteria.getDateDebut() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateCommande"), criteria.getDateDebut()));
            }

            if (criteria.getDateFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateCommande"), criteria.getDateFin()));
            }

            if (criteria.getFactureId() != null) {
                predicates.add(cb.equal(root.get("factureId"), criteria.getFactureId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
