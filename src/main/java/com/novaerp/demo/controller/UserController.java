package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.service.RoleService;
import com.novaerp.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // ==================== USER DASHBOARD ====================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/edit";
    }

    @PostMapping("/profile/edit")
    public String editProfileSubmit(@RequestParam String nom,
                                    @RequestParam String prenom,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        User currentUser = getAuthenticatedUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!currentUser.getEmail().equals(email.trim().toLowerCase())) {
            if (userService.findByEmail(email.trim().toLowerCase()).isPresent()) {
                model.addAttribute("error", "Cet email est déjà utilisé !");
                model.addAttribute("user", currentUser);
                return "user/edit";
            }
        }

        // Mise à jour des informations
        currentUser.setNom(nom);
        currentUser.setPrenom(prenom);
        currentUser.setEmail(email.trim().toLowerCase());
        currentUser.setPassword(password);

        User updatedUser = userService.update(currentUser);
        session.setAttribute("user", updatedUser);

        return "redirect:/user/profile";
    }

    // ==================== ADMIN - GESTION DES UTILISATEURS ====================

    @GetMapping("/manage")
    public String manageUsers(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String role,
                              HttpSession session,
                              Model model) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        List<User> users;

        // Recherche avec filtres
        if ((search != null && !search.trim().isEmpty()) || (role != null && !role.isEmpty())) {
            users = userService.searchUsers(search, role);
        } else {
            users = userService.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("user", getAuthenticatedUser(session));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("searchQuery", search);
        model.addAttribute("selectedRole", role);
        return "user/manage";
    }

    @GetMapping("/add")
    public String addUserForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("user", getAuthenticatedUser(session));
        return "user/add";
    }

    @PostMapping("/add")
    public String addUserSubmit(@RequestParam String nom,
                                @RequestParam String prenom,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam Long roleId,
                                HttpSession session,
                                Model model) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        // Vérifier si l'email existe déjà
        if (userService.findByEmail(email.trim().toLowerCase()).isPresent()) {
            model.addAttribute("error", "Cet email est déjà utilisé !");
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("user", getAuthenticatedUser(session));
            return "user/add";
        }

        // Créer le nouvel utilisateur
        User newUser = new User();
        newUser.setNom(nom);
        newUser.setPrenom(prenom);
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPassword(password);

        Role role = roleService.findById(roleId).orElse(null);
        if (role == null) {
            model.addAttribute("error", "Rôle invalide !");
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("user", getAuthenticatedUser(session));
            return "user/add";
        }

        newUser.setRole(role);
        userService.save(newUser);

        return "redirect:/user/manage";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        User userToEdit = userService.findById(id).orElse(null);
        if (userToEdit == null) {
            return "redirect:/user/manage";
        }

        List<Role> roles = roleService.findAll();
        model.addAttribute("userToEdit", userToEdit);
        model.addAttribute("roles", roles);
        model.addAttribute("user", getAuthenticatedUser(session));
        return "user/admin-edit";
    }

    @PostMapping("/edit/{id}")
    public String editUserSubmit(@PathVariable Long id,
                                 @RequestParam String nom,
                                 @RequestParam String prenom,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam Long roleId,
                                 HttpSession session,
                                 Model model) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        User userToEdit = userService.findById(id).orElse(null);
        if (userToEdit == null) {
            return "redirect:/user/manage";
        }

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        if (!userToEdit.getEmail().equals(email.trim().toLowerCase())) {
            if (userService.findByEmail(email.trim().toLowerCase()).isPresent()) {
                model.addAttribute("error", "Cet email est déjà utilisé !");
                model.addAttribute("userToEdit", userToEdit);
                model.addAttribute("roles", roleService.findAll());
                model.addAttribute("user", getAuthenticatedUser(session));
                return "user/admin-edit";
            }
        }

        // Mise à jour
        userToEdit.setNom(nom);
        userToEdit.setPrenom(prenom);
        userToEdit.setEmail(email.trim().toLowerCase());
        userToEdit.setPassword(password);

        Role role = roleService.findById(roleId).orElse(null);
        if (role != null) {
            userToEdit.setRole(role);
        }

        userService.update(userToEdit);

        return "redirect:/user/manage";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/user/dashboard";
        }

        User currentUser = getAuthenticatedUser(session);

        // Empêcher l'admin de se supprimer lui-même
        if (currentUser != null && currentUser.getId().equals(id)) {
            return "redirect:/user/manage?error=cannot-delete-self";
        }

        userService.deleteById(id);
        return "redirect:/user/manage";
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private User getAuthenticatedUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userService.findById(userId).orElse(null);
    }

    private boolean isAdmin(HttpSession session) {
        User user = getAuthenticatedUser(session);
        return user != null && user.getRole().getName() == RoleTypologie.ADMIN;
    }
}