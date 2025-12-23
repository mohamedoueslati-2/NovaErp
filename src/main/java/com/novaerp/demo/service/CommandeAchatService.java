package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.CommandeAchatSearchCriteria;
import com.novaerp.demo.model.entity.CommandeAchat;
import com.novaerp.demo.model.typologie.CommandeStatut;

import java.util.List;

public interface CommandeAchatService {

    CommandeAchat creerCommande(Long fournisseurId);

    CommandeAchat ajouterLigne(Long commandeId, Long produitId, Integer quantite, Double prixAchat);

    CommandeAchat terminerCommande(Long commandeId);

    void supprimerCommande(Long commandeId);

    void supprimerLigne(Long commandeId, Long ligneId);

    CommandeAchat modifierLigne(Long commandeId, Long ligneId, Integer quantite, Double prixAchat);

    List<CommandeAchat> findAll();

    CommandeAchat findById(Long id);

    CommandeAchat changerStatut(Long commandeId, CommandeStatut nouveauStatut);

    List<CommandeAchat> searchCommandes(CommandeAchatSearchCriteria criteria);
}
