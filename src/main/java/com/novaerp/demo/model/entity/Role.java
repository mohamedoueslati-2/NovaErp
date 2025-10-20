package com.novaerp.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.novaerp.demo.model.typologie.RoleTypologie;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom du rôle ne peut pas être nul.")
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleTypologie name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}