package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.ClientSearchCriteria;
import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ClientRepository;
import com.novaerp.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClientServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        when(clientRepository.findAll()).thenReturn(List.of(new Client()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void testSearch() {
        ClientSearchCriteria criteria = new ClientSearchCriteria();
        when(clientRepository.findAll(any(Specification.class))).thenReturn(List.of(new Client()));
        List<Client> result = service.search(criteria);
        assertEquals(1, result.size());
        verify(clientRepository).findAll(any(Specification.class));
    }

    @Test
    void testFindById_Success() {
        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        assertEquals(1L, service.findById(1L).getId());
    }

    @Test
    void testFindById_NotFound() {
        when(clientRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findById(2L));
    }

    @Test
    void testSave_Create() {
        Client client = new Client();
        client.setNom("Test");
        User user = new User();
        user.setId(10L);
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(user));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = service.save(client, "mail@test.com");
        assertEquals(user, result.getCreatedBy());
        assertEquals(user, result.getUpdatedBy());
        verify(clientRepository).save(client);
    }

    @Test
    void testSave_Update() {
        Client client = new Client();
        client.setId(1L);
        client.setNom("Test2");
        User user = new User();
        user.setId(11L);
        Client existing = new Client();
        existing.setId(1L);
        when(userRepository.findByEmail("mail2@test.com")).thenReturn(Optional.of(user));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(existing)).thenReturn(existing);

        Client result = service.save(client, "mail2@test.com");
        assertEquals(user, existing.getUpdatedBy());
        verify(clientRepository).save(existing);
    }

    @Test
    void testDeleteById_Success() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteById(1L));
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(clientRepository.existsById(2L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteById(2L));
    }

    @Test
    void testFindByCreatedById() {
        when(clientRepository.findByCreatedById(5L)).thenReturn(List.of(new Client()));
        assertEquals(1, service.findByCreatedById(5L).size());
    }
}
