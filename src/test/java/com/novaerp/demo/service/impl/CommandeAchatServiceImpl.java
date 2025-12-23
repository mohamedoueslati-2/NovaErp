package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.CommandeAchatSearchCriteria;
import com.novaerp.demo.model.entity.CommandeAchat;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.entity.LigneCommandeAchat;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.CommandeAchatRepository;
import com.novaerp.demo.repository.FactureRepository;
import com.novaerp.demo.repository.LigneCommandeAchatRepository;
import com.novaerp.demo.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandeAchatServiceImplTest {

    @Mock
    private CommandeAchatRepository commandeRepo;
    @Mock
    private LigneCommandeAchatRepository ligneRepo;
    @Mock
    private ProduitRepository produitRepo;
    @Mock
    private FactureRepository factureRepo;

    @InjectMocks
    private CommandeAchatServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreerCommande() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setFournisseurId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.save(any(CommandeAchat.class))).thenReturn(cmd);

        CommandeAchat result = service.creerCommande(1L);

        assertEquals(CommandeStatut.EN_COURS, result.getStatut());
        assertEquals(1L, result.getFournisseurId());
        verify(commandeRepo).save(any(CommandeAchat.class));
    }

    @Test
    void testAjouterLigne_Success() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        cmd.getLignes().clear();
        Produit produit = new Produit();
        produit.setId(2L);
        produit.setPrixAchatParDefaut(BigDecimal.valueOf(10));
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit));
        when(ligneRepo.save(any(LigneCommandeAchat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepo.save(any(CommandeAchat.class))).thenReturn(cmd);

        CommandeAchat result = service.ajouterLigne(1L, 2L, 5, 12.5);

        assertEquals(1, result.getLignes().size());
        verify(ligneRepo).save(any(LigneCommandeAchat.class));
        verify(commandeRepo).findById(1L);
    }

    @Test
    void testTerminerCommande_Success() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setProduitId(2L);
        ligne.setQuantite(2);
        ligne.setPrixAchat(10.0);
        cmd.getLignes().clear();
        cmd.getLignes().add(ligne);
        Produit produit = new Produit();
        produit.setId(2L);
        produit.setQuantite(10);
        produit.setPrixAchatParDefaut(BigDecimal.valueOf(8));
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(produitRepo.findById(2L)).thenReturn(Optional.of(produit));
        when(produitRepo.save(any(Produit.class))).thenReturn(produit);
        when(factureRepo.save(any(Facture.class))).thenAnswer(inv -> {
            Facture f = inv.getArgument(0);
            f.setId(99L);
            return f;
        });
        when(commandeRepo.save(any(CommandeAchat.class))).thenReturn(cmd);

        CommandeAchat result = service.terminerCommande(1L);

        assertEquals(CommandeStatut.TERMINEE, result.getStatut());
        assertEquals(99L, result.getFactureId());
        verify(produitRepo).save(any(Produit.class));
        verify(factureRepo).save(any(Facture.class));
    }

    @Test
    void testFindAll() {
        when(commandeRepo.findAll()).thenReturn(List.of(new CommandeAchat()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById_Success() {
        CommandeAchat cmd = new CommandeAchat();
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
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        doNothing().when(commandeRepo).delete(cmd);

        assertDoesNotThrow(() -> service.supprimerCommande(1L));
        verify(commandeRepo).delete(cmd);
    }

    @Test
    void testSupprimerLigne_Success() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeAchat ligne = new LigneCommandeAchat();
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
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setId(2L);
        ligne.setQuantite(1);
        ligne.setPrixAchat(5.0);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(ligneRepo.findById(2L)).thenReturn(Optional.of(ligne));
        when(ligneRepo.save(ligne)).thenReturn(ligne);
        when(commandeRepo.save(cmd)).thenReturn(cmd);

        CommandeAchat result = service.modifierLigne(1L, 2L, 10, 7.5);

        assertEquals(cmd, result);
        assertEquals(10, ligne.getQuantite());
        assertEquals(7.5, ligne.getPrixAchat());
    }

    @Test
    void testChangerStatut_Success() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.EN_COURS);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));
        when(commandeRepo.save(cmd)).thenReturn(cmd);

        CommandeAchat result = service.changerStatut(1L, CommandeStatut.ANNULEE);

        assertEquals(CommandeStatut.ANNULEE, result.getStatut());
    }

    @Test
    void testChangerStatut_Terminee() {
        CommandeAchat cmd = new CommandeAchat();
        cmd.setId(1L);
        cmd.setStatut(CommandeStatut.TERMINEE);
        when(commandeRepo.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class, () -> service.changerStatut(1L, CommandeStatut.ANNULEE));
    }

    @Test
    void testSearch() {
        CommandeAchatSearchCriteria criteria = new CommandeAchatSearchCriteria();
        List<CommandeAchat> list = List.of(new CommandeAchat());
        when(commandeRepo.findAll(any(Specification.class))).thenReturn(list);

        List<CommandeAchat> result = service.searchCommandes(criteria);

        assertEquals(1, result.size());
        verify(commandeRepo).findAll(any(Specification.class));
    }
}
