package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test.user@mail.com");
        testUser.setNom("Test");
        testUser.setPrenom("User");
        testUser.setPassword("password");
    }

    // Méthode utilitaire pour créer un produit valide
    private Produit createDefaultProduit() {
        Produit produit = new Produit();
        produit.setNom("ProduitTest");
        produit.setReference("REF001"); // obligatoire
        produit.setQuantite(10);
        produit.setPrixAchatParDefaut(BigDecimal.valueOf(100.0));
        produit.setPrixVente(BigDecimal.valueOf(150.0));
        return produit;
    }

    @Test
    void testSaveProduit() {
        Produit produit = createDefaultProduit();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> {
            Produit p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Produit savedProduit = produitService.save(produit, testUser.getEmail());

        assertNotNull(savedProduit.getId());
        assertEquals(testUser.getEmail(), savedProduit.getCreatedBy().getEmail());

        verify(produitRepository, times(1)).save(produit);
    }

    @Test
    void testAdjustStock() {
        Produit produit = createDefaultProduit();
        produit.setId(1L);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(produitRepository.save(any(Produit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produit updatedProduit = produitService.adjustStock(1L, 5, testUser.getEmail());

        assertEquals(15, updatedProduit.getQuantite());
        assertEquals(testUser.getEmail(), updatedProduit.getUpdatedBy().getEmail());

        verify(produitRepository, times(1)).save(produit);
    }

    @Test
    void testDeleteProduit() {
        Produit produit = createDefaultProduit();
        produit.setId(1L);

        when(produitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(produitRepository).deleteById(1L);

        assertDoesNotThrow(() -> produitService.deleteById(1L));

        verify(produitRepository, times(1)).deleteById(1L);
    }
}
