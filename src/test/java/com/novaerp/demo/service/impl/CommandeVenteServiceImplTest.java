package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.CommandeVenteSearchCriteria;
import com.novaerp.demo.model.entity.CommandeVente;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.entity.LigneCommandeVente;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.CommandeVenteRepository;
import com.novaerp.demo.repository.FactureRepository;
import com.novaerp.demo.repository.LigneCommandeVenteRepository;
import com.novaerp.demo.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandeVenteServiceImplTest {

    @Mock
    private CommandeVenteRepository commandeRepo;
    @Mock
    private LigneCommandeVenteRepository ligneRepo;
    @Mock
    private ProduitRepository produitRepo;
    @Mock
    private FactureRepository factureRepo;

    @InjectMocks
    private CommandeVenteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreerCommande() {
        CommandeVente cmd = new CommandeVente();
        cmd.setClientId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.save(any(CommandeVente.class))).thenReturn(cmd);

        CommandeVente result = service.creerCommande(1L);

        assertEquals(CommandeStatut.EN_COURS, result.getStatut());
        assertEquals(1L, result.getClientId());
        verify(commandeRepo).save(any(CommandeVente.class));
    }

    @Test
    void testAjouterLigne_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        cmd.getLignes().clear();
        Produit produit = new Produit();
        produit.setId(2L);
        produit.setPrixVente(BigDecimal.valueOf(10));
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit));
        when(ligneRepo.save(any(LigneCommandeVente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepo.save(any(CommandeVente.class))).thenReturn(cmd);

        CommandeVente result = service.ajouterLigne(1L, 2L, 5);

        assertEquals(1, result.getLignes().size());
        verify(ligneRepo).save(any(LigneCommandeVente.class));
        verify(commandeRepo, times(1)).findById(1L); // Correction ici
    }

    @Test
    void testAjouterLigne_CommandeTerminee() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.TERMINEE);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.ajouterLigne(1L, 2L, 5));
        assertTrue(ex.getMessage().contains("Impossible de modifier une commande terminée"));
    }

    @Test
    void testTerminerCommande_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeVente ligne = new LigneCommandeVente();
        ligne.setProduitId(2L);
        ligne.setQuantite(2);
        cmd.getLignes().clear();
        cmd.getLignes().add(ligne);
        Produit produit = new Produit();
        produit.setId(2L);
        produit.setQuantite(10);
        produit.setNom("ProduitTest");
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit));
        when(produitRepo.save(any(Produit.class))).thenReturn(produit);
        when(factureRepo.save(any(Facture.class))).thenAnswer(inv -> {
            Facture f = inv.getArgument(0);
            f.setId(99L);
            return f;
        });
        when(commandeRepo.save(any(CommandeVente.class))).thenReturn(cmd);

        CommandeVente result = service.terminerCommande(1L);

        assertEquals(CommandeStatut.TERMINEE, result.getStatut());
        assertEquals(99L, result.getFactureId());
        verify(produitRepo).save(any(Produit.class));
        verify(factureRepo).save(any(Facture.class));
    }

    @Test
    void testTerminerCommande_StockInsuffisant() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeVente ligne = new LigneCommandeVente();
        ligne.setProduitId(2L);
        ligne.setQuantite(5);
        cmd.getLignes().clear();
        cmd.getLignes().add(ligne);
        Produit produit = new Produit();
        produit.setId(2L);
        produit.setQuantite(2);
        produit.setNom("ProduitTest");
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.terminerCommande(1L));
        assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }

    @Test
    void testFindAll() {
        when(commandeRepo.findAll()).thenReturn(List.of(new CommandeVente()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        assertEquals(1L, service.findById(1L).getId());
    }

    @Test
    void testFindById_NotFound() {
        when(commandeRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findById(2L));
    }

    @Test
    void testSupprimerCommande_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        doNothing().when(commandeRepo).delete(cmd);

        assertDoesNotThrow(() -> service.supprimerCommande(1L));
        verify(commandeRepo).delete(cmd);
    }

    @Test
    void testSupprimerCommande_Terminee() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.TERMINEE);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class, () -> service.supprimerCommande(1L));
    }

    @Test
    void testSupprimerLigne_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeVente ligne = new LigneCommandeVente();
        ligne.setId(2L);
        cmd.getLignes().clear();
        cmd.getLignes().add(ligne);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(ligneRepo.findById(2L)).thenReturn(Optional.of(ligne));
        doNothing().when(ligneRepo).delete(ligne);
        when(commandeRepo.save(cmd)).thenReturn(cmd);

        assertDoesNotThrow(() -> service.supprimerLigne(1L, 2L));
        verify(ligneRepo).delete(ligne);
    }

    @Test
    void testModifierLigne_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeVente ligne = new LigneCommandeVente();
        ligne.setId(2L);
        ligne.setQuantite(1);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(ligneRepo.findById(2L)).thenReturn(Optional.of(ligne));
        when(ligneRepo.save(ligne)).thenReturn(ligne);
        when(commandeRepo.save(cmd)).thenReturn(cmd);

        CommandeVente result = service.modifierLigne(1L, 2L, 10);

        assertEquals(cmd, result);
        assertEquals(10, ligne.getQuantite());
    }

    @Test
    void testChangerStatut_Success() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(commandeRepo.save(cmd)).thenReturn(cmd);

        CommandeVente result = service.changerStatut(1L, CommandeStatut.ANNULEE);

        assertEquals(CommandeStatut.ANNULEE, result.getStatut());
    }

    @Test
    void testChangerStatut_Terminee() {
        CommandeVente cmd = new CommandeVente();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.TERMINEE);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class, () -> service.changerStatut(1L, CommandeStatut.ANNULEE));
    }

    @Test
    void testSearch() {
        CommandeVenteSearchCriteria criteria = new CommandeVenteSearchCriteria();
        List<CommandeVente> list = List.of(new CommandeVente());
        when(commandeRepo.findAll(any(Specification.class))).thenReturn(list);

        List<CommandeVente> result = service.search(criteria);

        assertEquals(1, result.size());
        verify(commandeRepo).findAll(any(Specification.class));
    }
}
