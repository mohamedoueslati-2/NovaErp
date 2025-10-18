package com.novaerp.demo.model.dto;

import com.novaerp.demo.model.typologie.RoleTypologie;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse e-mail doit être valide")
    private String email;

    private String password;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleTypologie role;
}