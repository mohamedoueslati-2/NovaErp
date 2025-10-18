package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.User;
import java.util.Optional;

public interface UserService {
    User login(String email, String password);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User update(User user);
}
