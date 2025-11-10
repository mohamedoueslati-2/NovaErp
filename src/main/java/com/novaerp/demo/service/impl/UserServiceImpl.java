package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        log.info("Récupération de tous les utilisateurs");
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        log.info("Recherche de l'utilisateur avec l'ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    @Override
    public User findByEmail(String email) {
        log.info("Recherche de l'utilisateur avec l'email: {}", email);
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Création d'un nouvel utilisateur: {}", user.getEmail());

        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleTypologie.STOCK)
                    .orElseThrow(() -> new RuntimeException("Rôle par défaut non trouvé"));
            user.getRoles().add(defaultRole);
        }

        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public User update(Long id, User updatedUser) {
        log.info("Mise à jour de l'utilisateur avec l'ID: {}", id);

        User existingUser = findById(id);

        existingUser.setNom(updatedUser.getNom());
        existingUser.setPrenom(updatedUser.getPrenom());

        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        User saved = userRepository.save(existingUser);
        log.info("Utilisateur mis à jour avec succès: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Suppression de l'utilisateur avec l'ID: {}", id);
        User user = findById(id);
        userRepository.delete(user);
        log.info("Utilisateur supprimé avec succès: {}", id);
    }

    @Override
    @Transactional
    public void updateProfile(String email, User updatedUser) {
        log.info("Mise à jour du profil de l'utilisateur: {}", email);

        User existingUser = findByEmail(email);
        existingUser.setNom(updatedUser.getNom());
        existingUser.setPrenom(updatedUser.getPrenom());

        // Vérifier si l'email a changé
        if (updatedUser.getEmail() != null && !existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (existsByEmail(updatedUser.getEmail())) {
                throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Encoder le mot de passe uniquement s'il est fourni
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existingUser);
        log.info("Profil mis à jour avec succès pour: {}", email);
    }

    @Override
    @Transactional
    public void assignRole(Long userId, RoleTypologie roleTypologie) {
        log.info("Attribution du rôle {} à l'utilisateur {}", roleTypologie, userId);

        User user = findById(userId);
        Role role = roleRepository.findByName(roleTypologie)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleTypologie));

        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Rôle {} attribué avec succès à l'utilisateur {}", roleTypologie, userId);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, RoleTypologie roleTypologie) {
        log.info("Retrait du rôle {} de l'utilisateur {}", roleTypologie, userId);

        User user = findById(userId);

        if (user.getRoles().size() <= 1) {
            throw new RuntimeException("Impossible de retirer le dernier rôle de l'utilisateur");
        }

        Role role = roleRepository.findByName(roleTypologie)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleTypologie));

        user.getRoles().remove(role);
        userRepository.save(user);
        log.info("Rôle {} retiré avec succès de l'utilisateur {}", roleTypologie, userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}