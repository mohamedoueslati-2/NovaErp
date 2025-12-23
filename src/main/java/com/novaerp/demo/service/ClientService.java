package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.ClientSearchCriteria;
import com.novaerp.demo.model.entity.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();
    List<Client> search(ClientSearchCriteria criteria);
    Client findById(Long id);
    Client save(Client client, String currentUserEmail);
    void deleteById(Long id);
    List<Client> findByCreatedById(Long userId);
}
