package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.FactureSearchCriteria;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.FactureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FactureServiceImplTest {

    @Mock
    private FactureRepository factureRepository;

    @InjectMocks
    private FactureServiceImpl factureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindWithFilters() {
        FactureSearchCriteria criteria = new FactureSearchCriteria();
        criteria.setNumeroFacture("F123");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Facture> page = new PageImpl<>(Collections.emptyList());

        when(factureRepository.findAll((org.springframework.data.jpa.domain.Specification<Facture>) any(), eq(pageable))).thenReturn(page);

        Page<Facture> result = factureService.findWithFilters(criteria, pageable);

        assertNotNull(result);
        verify(factureRepository).findAll((org.springframework.data.jpa.domain.Specification<Facture>) any(), eq(pageable));
    }

    @Test
    void testFindById_Success() {
        Facture facture = new Facture();
        facture.setId(1L);
        when(factureRepository.findById(1L)).thenReturn(Optional.of(facture));

        Facture found = factureService.findById(1L);

        assertEquals(1L, found.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(factureRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> factureService.findById(2L));
    }

    @Test
    void testSave() {
        Facture facture = new Facture();
        when(factureRepository.save(facture)).thenReturn(facture);

        Facture saved = factureService.save(facture);

        assertNotNull(saved);
        verify(factureRepository).save(facture);
    }

    @Test
    void testDeleteById() {
        doNothing().when(factureRepository).deleteById(1L);

        assertDoesNotThrow(() -> factureService.deleteById(1L));
        verify(factureRepository).deleteById(1L);
    }

    @Test
    void testFindFacturesNonPayees() {
        List<Facture> factures = Arrays.asList(new Facture(), new Facture());
        when(factureRepository.findByStatutAndType("NON_PAYEE", FactureType.FACTURE_CLIENT)).thenReturn(factures);

        List<Facture> result = factureService.findFacturesNonPayees(FactureType.FACTURE_CLIENT);

        assertEquals(2, result.size());
    }

    @Test
    void testGetFactureStatistics() {
        Facture f1 = new Facture();
        f1.setStatut("PAYEE");
        f1.setMontantTTC(100.0);
        Facture f2 = new Facture();
        f2.setStatut("NON_PAYEE");
        f2.setMontantTTC(50.0);

        List<Facture> all = Arrays.asList(f1, f2);
        when(factureRepository.findAllByType(FactureType.FACTURE_CLIENT)).thenReturn(all);

        Map<String, Object> stats = factureService.getFactureStatistics(FactureType.FACTURE_CLIENT);

        assertEquals(2, stats.get("totalFactures"));
        assertEquals(1L, stats.get("facturesPayees"));
        assertEquals(1L, stats.get("facturesNonPayees"));
        assertEquals(150.0, (double) stats.get("montantTotal"), 0.01);
    }
}
