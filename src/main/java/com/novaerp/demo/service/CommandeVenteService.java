package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.CommandeVenteSearchCriteria;
import com.novaerp.demo.model.entity.CommandeVente;
import com.novaerp.demo.model.typologie.CommandeStatut;

import java.util.List;

public interface CommandeVenteService {

    CommandeVente creerCommande(Long clientId);

    CommandeVente ajouterLigne(Long commandeId, Long produitId, Integer quantite);

    CommandeVente terminerCommande(Long commandeId);

    void supprimerCommande(Long commandeId);

    void supprimerLigne(Long commandeId, Long ligneId);

    CommandeVente modifierLigne(Long commandeId, Long ligneId, Integer quantite);

    List<CommandeVente> findAll();

    CommandeVente findById(Long id);

    CommandeVente changerStatut(Long commandeId, CommandeStatut nouveauStatut);

    List<CommandeVente> search(CommandeVenteSearchCriteria criteria);
}
