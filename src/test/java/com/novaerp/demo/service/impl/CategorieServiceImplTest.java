package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.CategorieRepository;
import com.novaerp.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategorieServiceImplTest {

    @Mock
    private CategorieRepository categorieRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategorieServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(categorieRepository.findAllByOrderByOrdreAffichageAsc()).thenReturn(List.of(new Categorie()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testFindById_Success() {
        Categorie cat = new Categorie();
        cat.setId(1L);
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(cat));
        assertEquals(1L, service.findById(1L).getId());
    }

    @Test
    void testFindById_NotFound() {
        when(categorieRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findById(2L));
    }

    @Test
    void testSave_Create() {
        Categorie cat = new Categorie();
        User user = new User();
        user.setId(10L);
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(user));
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(inv -> inv.getArgument(0));

        Categorie result = service.save(cat, "mail@test.com");
        assertEquals(user, result.getCreatedBy());
        verify(categorieRepository).save(cat);
    }

    @Test
    void testSave_Update() {
        Categorie cat = new Categorie();
        cat.setId(1L);
        User user = new User();
        user.setId(11L);
        when(userRepository.findByEmail("mail2@test.com")).thenReturn(Optional.of(user));
        when(categorieRepository.save(cat)).thenReturn(cat);

        Categorie result = service.save(cat, "mail2@test.com");
        assertEquals(user, cat.getUpdatedBy());
        verify(categorieRepository).save(cat);
    }

    @Test
    void testDeleteById_Success() {
        when(categorieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categorieRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteById(1L));
        verify(categorieRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(categorieRepository.existsById(2L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteById(2L));
    }

    @Test
    void testFindByCreatedById() {
        when(categorieRepository.findByCreatedById(5L)).thenReturn(List.of(new Categorie()));
        assertEquals(1, service.findByCreatedById(5L).size());
    }
}
