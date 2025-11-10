package com.novaerp.demo.service;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    Role findById(Long id);

    Role findByName(RoleTypologie name);

    Role save(Role role);

    Role update(Long id, Role updatedRole);

    void delete(Long id);

    void initDefaultRoles();
}