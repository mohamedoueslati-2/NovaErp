package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.FournisseurSearchCriteria;
import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FournisseurServiceImplTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FournisseurServiceImpl fournisseurService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@mail.com");
    }

    private Fournisseur createFournisseur() {
        Fournisseur f = new Fournisseur();
        f.setId(1L);
        f.setCode("F001");
        f.setNom("Fournisseur Test");
        f.setEmail("fournisseur@mail.com");
        f.setTelephone("123456789");
        f.setAdresse("Adresse");
        return f;
    }

    @Test
    void testSave_NewFournisseur() {
        Fournisseur fournisseur = createFournisseur();
        fournisseur.setId(null);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(fournisseurRepository.save(any(Fournisseur.class))).thenAnswer(inv -> {
            Fournisseur f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        Fournisseur saved = fournisseurService.save(fournisseur, testUser.getEmail());

        assertNotNull(saved.getId());
        assertEquals(testUser, saved.getCreatedBy());
        verify(fournisseurRepository, times(1)).save(fournisseur);
    }

    @Test
    void testSave_UpdateFournisseur() {
        Fournisseur fournisseur = createFournisseur();
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        Fournisseur updated = fournisseurService.save(fournisseur, testUser.getEmail());

        assertEquals(testUser, updated.getUpdatedBy());
        verify(fournisseurRepository, times(1)).save(fournisseur);
    }

    @Test
    void testDeleteById_Success() {
        when(fournisseurRepository.existsById(1L)).thenReturn(true);
        doNothing().when(fournisseurRepository).deleteById(1L);

        assertDoesNotThrow(() -> fournisseurService.deleteById(1L));
        verify(fournisseurRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(fournisseurRepository.existsById(2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> fournisseurService.deleteById(2L));
    }

    @Test
    void testFindById_Success() {
        Fournisseur fournisseur = createFournisseur();
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));

        Fournisseur found = fournisseurService.findById(1L);

        assertEquals("Fournisseur Test", found.getNom());
    }

    @Test
    void testFindById_NotFound() {
        when(fournisseurRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fournisseurService.findById(2L));
    }

    @Test
    void testFindAll() {
        when(fournisseurRepository.findAll()).thenReturn(Collections.singletonList(createFournisseur()));

        assertEquals(1, fournisseurService.findAll().size());
    }

    @Test
    void testFindByCreatedById() {
        when(fournisseurRepository.findByCreatedById(1L)).thenReturn(Collections.singletonList(createFournisseur()));

        assertEquals(1, fournisseurService.findByCreatedById(1L).size());
    }
}
