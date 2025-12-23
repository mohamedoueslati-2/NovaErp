package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRole_Success() {
        Role role = new Role();
        role.setName(RoleTypologie.ADMIN);

        when(roleRepository.existsByName(RoleTypologie.ADMIN)).thenReturn(false);
        when(roleRepository.save(role)).thenAnswer(invocation -> {
            Role r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        Role saved = roleService.save(role);

        assertNotNull(saved.getId());
        assertEquals(RoleTypologie.ADMIN, saved.getName());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testSaveRole_AlreadyExists() {
        Role role = new Role();
        role.setName(RoleTypologie.ADMIN);

        when(roleRepository.existsByName(RoleTypologie.ADMIN)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> roleService.save(role));
        assertTrue(ex.getMessage().contains("existe déjà"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleTypologie.STOCK);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role found = roleService.findById(1L);

        assertEquals(1L, found.getId());
        assertEquals(RoleTypologie.STOCK, found.getName());
    }

    @Test
    void testFindById_NotFound() {
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.findById(2L));
    }
}
