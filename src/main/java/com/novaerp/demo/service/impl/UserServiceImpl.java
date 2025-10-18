package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public User update(User user) {
        return userRepository.save(user);
    }
}