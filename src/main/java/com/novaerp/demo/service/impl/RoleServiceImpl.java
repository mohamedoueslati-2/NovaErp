package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import com.novaerp.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        log.info("Récupération de tous les rôles");
        return roleRepository.findAll();
    }

    @Override
    public Role findById(Long id) {
        log.info("Recherche du rôle avec l'ID: {}", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé avec l'ID: " + id));
    }

    @Override
    public Role findByName(RoleTypologie name) {
        log.info("Recherche du rôle: {}", name);
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + name));
    }

    @Override
    @Transactional
    public Role save(Role role) {
        log.info("Création d'un nouveau rôle: {}", role.getName());

        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Un rôle avec ce nom existe déjà: " + role.getName());
        }

        Role saved = roleRepository.save(role);
        log.info("Rôle créé avec succès: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Role update(Long id, Role updatedRole) {
        log.info("Mise à jour du rôle avec l'ID: {}", id);

        Role existingRole = findById(id);
        existingRole.setName(updatedRole.getName());

        Role saved = roleRepository.save(existingRole);
        log.info("Rôle mis à jour avec succès: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Suppression du rôle avec l'ID: {}", id);
        Role role = findById(id);

        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer un rôle assigné à des utilisateurs");
        }

        roleRepository.delete(role);
        log.info("Rôle supprimé avec succès: {}", id);
    }

    @Override
    @Transactional
    public void initDefaultRoles() {
        log.info("Initialisation des rôles par défaut");

        for (RoleTypologie roleType : RoleTypologie.values()) {
            if (!roleRepository.existsByName(roleType)) {
                Role role = new Role();
                role.setName(roleType);
                roleRepository.save(role);
                log.info("Rôle créé: {}", roleType);
            }
        }

        log.info("Initialisation des rôles terminée");
    }
}