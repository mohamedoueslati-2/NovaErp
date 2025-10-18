package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Initialiser le rôle pour éviter LazyInitializationException
            if (user.getRole() != null) {
                user.getRole().getName();
            }
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        // Initialiser le rôle
        userOpt.ifPresent(user -> {
            if (user.getRole() != null) {
                user.getRole().getName();
            }
        });
        return userOpt;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        // Initialiser les rôles
        users.forEach(user -> {
            if (user.getRole() != null) {
                user.getRole().getName();
            }
        });
        return users;
    }

    @Override
    public List<User> searchUsers(String search, String role) {
        List<User> users = findAll();

        // Filtrer par recherche (nom, prénom, email)
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.trim().toLowerCase();
            users = users.stream()
                    .filter(user ->
                            user.getNom().toLowerCase().contains(searchLower) ||
                                    user.getPrenom().toLowerCase().contains(searchLower) ||
                                    user.getEmail().toLowerCase().contains(searchLower)
                    )
                    .collect(Collectors.toList());
        }

        // Filtrer par rôle
        if (role != null && !role.isEmpty()) {
            try {
                RoleTypologie roleType = RoleTypologie.valueOf(role);
                users = users.stream()
                        .filter(user -> user.getRole().getName() == roleType)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Rôle invalide, ignorer le filtre
            }
        }

        return users;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}