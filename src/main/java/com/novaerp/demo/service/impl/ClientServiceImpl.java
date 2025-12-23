package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.ClientSearchCriteria;
import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.ClientRepository;
import com.novaerp.demo.repository.ClientSpecification;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> search(ClientSearchCriteria criteria) {
        Specification<Client> spec = ClientSpecification.withCriteria(criteria);
        return clientRepository.findAll(spec);
    }

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID : " + id));
    }

    @Override
    @Transactional
    public Client save(Client client, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (client.getId() != null) {
            Client existing = clientRepository.findById(client.getId())
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            existing.setCode(client.getCode());
            existing.setNom(client.getNom());
            existing.setEmail(client.getEmail());
            existing.setTelephone(client.getTelephone());
            existing.setAdresse(client.getAdresse());
            existing.setUpdatedBy(currentUser);

            return clientRepository.save(existing);
        } else {
            client.setCreatedBy(currentUser);
            client.setUpdatedBy(currentUser);
            return clientRepository.save(client);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'ID : " + id);
        }
        clientRepository.deleteById(id);
    }

    @Override
    public List<Client> findByCreatedById(Long userId) {
        return clientRepository.findByCreatedById(userId);
    }
}
