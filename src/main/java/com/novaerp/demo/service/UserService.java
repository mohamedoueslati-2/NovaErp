package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User login(String email, String password);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> searchUsers(String search, String role);
    User save(User user);
    User update(User user);
    void deleteById(Long id);
}