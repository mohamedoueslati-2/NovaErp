package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findById(Long id);
    Optional<Role> findByName(RoleTypologie name);
    List<Role> findAll();
}