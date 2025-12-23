package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User findByEmail(String email);
    User save(User user);
    User update(Long id, User updatedUser);
    void delete(Long id);
    void updateProfile(String email, User updatedUser);
    void assignRole(Long userId, RoleTypologie roleTypologie);
    void removeRole(Long userId, RoleTypologie roleTypologie);
    boolean existsByEmail(String email);
    List<Categorie> getCategoriesByUser(Long userId);
    List<Fournisseur> getFournisseursByUser(Long userId);
}
