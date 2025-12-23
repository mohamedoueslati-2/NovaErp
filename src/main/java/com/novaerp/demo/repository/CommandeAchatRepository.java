package com.novaerp.demo.repository;

import com.novaerp.demo.model.entity.CommandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommandeAchatRepository extends JpaRepository<CommandeAchat, Long>, JpaSpecificationExecutor<CommandeAchat> {
}
