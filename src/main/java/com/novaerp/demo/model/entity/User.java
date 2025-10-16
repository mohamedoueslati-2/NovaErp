package com.novaerp.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom ne peut pas être nul.")
    private String nom;

    @NotNull(message = "Le prénom ne peut pas être nul.")
    private String prenom;

    @Email(message = "L'adresse e-mail doit être valide.")
    @NotNull(message = "L'email ne peut pas être nul.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Le mot de passe ne peut pas être nul.")
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Clé étrangère vers Role
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    private List<Produit> produits;

    @OneToMany(mappedBy = "createdBy")
    private List<Categorie> categories;

    @OneToMany(mappedBy = "createdBy")
    private List<Fournisseur> fournisseurs;

    @OneToMany(mappedBy = "createdBy")
    private List<Client> clients;


}