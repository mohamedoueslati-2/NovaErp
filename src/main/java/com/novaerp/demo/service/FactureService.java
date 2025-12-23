package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.FactureSearchCriteria;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FactureService {
    Page<Facture> findWithFilters(FactureSearchCriteria criteria, Pageable pageable);
    Facture findById(Long id);
    Facture save(Facture facture);
    void deleteById(Long id);

    // Spécifiques par type
    List<Facture> findFacturesNonPayees(FactureType type);
    Map<String, Object> getFactureStatistics(FactureType type);
}