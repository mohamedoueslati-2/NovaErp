package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Role;
import com.novaerp.demo.model.typologie.RoleTypologie;
import com.novaerp.demo.repository.RoleRepository;
import com.novaerp.demo.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(RoleTypologie name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}