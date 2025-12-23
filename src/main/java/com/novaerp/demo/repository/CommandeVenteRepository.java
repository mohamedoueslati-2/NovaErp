package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.CommandeVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommandeVenteRepository extends JpaRepository<CommandeVente, Long>,
        JpaSpecificationExecutor<CommandeVente> {
}
