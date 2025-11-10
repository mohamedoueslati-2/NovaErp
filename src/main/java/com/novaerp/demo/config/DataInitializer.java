package com.novaerp.demo.config;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Démarrage de l'initialisation des données...");

        // Initialiser les rôles
        roleService.initDefaultRoles();

        // Créer un utilisateur admin par défaut
        if (!userRepository.existsByEmail("admin@novaerp.com")) {
            User admin = new User();
            admin.setNom("Admin");
            admin.setPrenom("System");
            admin.setEmail("admin@novaerp.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleTypologie.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé")));
            admin.setRoles(roles);

            userRepository.save(admin);
            log.info("Utilisateur admin créé: admin@novaerp.com / admin123");
        }

        // Créer un utilisateur stock par défaut
        if (!userRepository.existsByEmail("stock@novaerp.com")) {
            User stock = new User();
            stock.setNom("Stock");
            stock.setPrenom("Manager");
            stock.setEmail("stock@novaerp.com");
            stock.setPassword(passwordEncoder.encode("stock123"));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleTypologie.STOCK)
                    .orElseThrow(() -> new RuntimeException("Rôle STOCK non trouvé")));
            stock.setRoles(roles);

            userRepository.save(stock);
            log.info("Utilisateur stock créé: stock@novaerp.com / stock123");
        }

        // Créer un utilisateur vente par défaut
        if (!userRepository.existsByEmail("vente@novaerp.com")) {
            User vente = new User();
            vente.setNom("Vente");
            vente.setPrenom("Manager");
            vente.setEmail("vente@novaerp.com");
            vente.setPassword(passwordEncoder.encode("vente123"));

            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleTypologie.VENTE)
                    .orElseThrow(() -> new RuntimeException("Rôle VENTE non trouvé")));
            vente.setRoles(roles);

            userRepository.save(vente);
            log.info("Utilisateur vente créé: vente@novaerp.com / vente123");
        }

        log.info("Initialisation des données terminée");
    }
}