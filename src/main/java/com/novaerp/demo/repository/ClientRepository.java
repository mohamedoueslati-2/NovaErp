package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByCode(String code);
    Optional<Client> findByEmail(String email);
    List<Client> findByCreatedById(Long userId);
}
