package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import com.novaerp.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveUser() {
        // Préparer rôle par défaut
        if (roleRepository.findByName(RoleTypologie.STOCK).isEmpty()) {
            Role role = new Role();
            role.setName(RoleTypologie.STOCK);
            roleRepository.save(role);
        }

        // Créer utilisateur
        User user = new User();
        user.setNom("Test");
        user.setPrenom("User");
        user.setEmail("test.user@mail.com");
        user.setPassword("123456");

        User saved = userService.save(user);

        // Vérifications
        assertNotNull(saved.getId());
        assertEquals("test.user@mail.com", saved.getEmail());
        assertFalse(saved.getRoles().isEmpty());
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setNom("Find");
        user.setPrenom("Me");
        user.setEmail("find.me@mail.com");
        user.setPassword("123");

        User saved = userService.save(user);

        User found = userService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setNom("Delete");
        user.setPrenom("Me");
        user.setEmail("delete.me@mail.com");
        user.setPassword("123");

        User saved = userService.save(user);

        userService.delete(saved.getId());

        assertThrows(RuntimeException.class,
                () -> userService.findById(saved.getId()));
    }
}
