package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.FactureSearchCriteria;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.FactureRepository;
import com.novaerp.demo.service.FactureService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    @Override
    public Page<Facture> findWithFilters(FactureSearchCriteria criteria, Pageable pageable) {
        Specification<Facture> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getNumeroFacture() != null && !criteria.getNumeroFacture().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("numeroFactureFournisseur")),
                        "%" + criteria.getNumeroFacture().toLowerCase() + "%"));
            }

            if (criteria.getDateDebut() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateFacture"), criteria.getDateDebut()));
            }

            if (criteria.getDateFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateFacture"), criteria.getDateFin()));
            }

            if (criteria.getStatut() != null && !criteria.getStatut().isEmpty()) {
                predicates.add(cb.equal(root.get("statut"), criteria.getStatut()));
            }

            if (criteria.getCommandeId() != null) {
                predicates.add(cb.equal(root.get("commandeId"), criteria.getCommandeId()));
            }

            if (criteria.getType() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.getType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return factureRepository.findAll(spec, pageable);
    }

    @Override
    public Facture findById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
    }

    @Override
    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    @Override
    public void deleteById(Long id) {
        factureRepository.deleteById(id);
    }

    @Override
    public List<Facture> findFacturesNonPayees(FactureType type) {
        return factureRepository.findByStatutAndType("NON_PAYEE", type);
    }

    @Override
    public Map<String, Object> getFactureStatistics(FactureType type) {
        Map<String, Object> stats = new HashMap<>();
        List<Facture> allFactures = factureRepository.findAllByType(type);

        stats.put("totalFactures", allFactures.size());
        stats.put("facturesPayees", allFactures.stream().filter(f -> "PAYEE".equals(f.getStatut())).count());
        stats.put("facturesNonPayees", allFactures.stream().filter(f -> "NON_PAYEE".equals(f.getStatut())).count());
        stats.put("montantTotal", allFactures.stream().mapToDouble(f -> f.getMontantTTC() != null ? f.getMontantTTC() : 0).sum());

        return stats;
    }
}