package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.CommandeVenteSearchCriteria;
import com.novaerp.demo.model.entity.CommandeVente;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.entity.LigneCommandeVente;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.CommandeVenteRepository;
import com.novaerp.demo.repository.CommandeVenteSpecification;
import com.novaerp.demo.repository.FactureRepository;
import com.novaerp.demo.repository.LigneCommandeVenteRepository;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.service.CommandeVenteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CommandeVenteServiceImpl implements CommandeVenteService {

    @Autowired
    private CommandeVenteRepository commandeRepo;

    @Autowired
    private LigneCommandeVenteRepository ligneRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @Autowired
    private FactureRepository factureRepo;

    @Override
    @Transactional
    public CommandeVente creerCommande(Long clientId) {
        CommandeVente c = new CommandeVente();
        c.setClientId(clientId);
        c.setStatut(CommandeStatut.EN_COURS);
        return commandeRepo.save(c);
    }

    @Override
    @Transactional
    public CommandeVente ajouterLigne(Long commandeId, Long produitId, Integer quantite) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de modifier une commande terminée");
        }

        Produit p = produitRepo.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + produitId));

        LigneCommandeVente ligne = new LigneCommandeVente();
        ligne.setProduitId(produitId);
        ligne.setQuantite(quantite);
        ligne.setPrixVente(p.getPrixVente());

        cmd.addLigne(ligne);

        ligneRepo.save(ligne);
        return commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeVente terminerCommande(Long commandeId) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Commande déjà terminée");
        }

        cmd.getLignes().forEach(l -> {
            Produit p = produitRepo.findById(l.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + l.getProduitId()));

            int stockActuel = p.getQuantite() == null ? 0 : p.getQuantite();
            if (stockActuel < l.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour le produit " + p.getNom());
            }
            p.setQuantite(stockActuel - l.getQuantite());
            produitRepo.save(p);
        });

        Facture facture = new Facture();
        facture.setDateFacture(LocalDate.now());
        facture.setCommandeId(cmd.getId());
        BigDecimal total = cmd.getTotal() == null ? BigDecimal.ZERO : cmd.getTotal();
        facture.setMontantTTC(total.doubleValue());
        facture.setType(FactureType.FACTURE_CLIENT);
        facture.setStatut("NON_PAYEE");
        facture = factureRepo.save(facture);

        cmd.setFactureId(facture.getId());
        cmd.setStatut(CommandeStatut.TERMINEE);

        return commandeRepo.save(cmd);
    }

    @Override
    public List<CommandeVente> findAll() {
        return commandeRepo.findAll();
    }

    @Override
    public CommandeVente findById(Long id) {
        return commandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + id));
    }

    @Override
    @Transactional
    public void supprimerCommande(Long commandeId) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de supprimer une commande terminée");
        }

        commandeRepo.delete(cmd);
    }

    @Override
    @Transactional
    public void supprimerLigne(Long commandeId, Long ligneId) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de modifier une commande terminée");
        }

        LigneCommandeVente ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée : " + ligneId));

        cmd.removeLigne(ligne);
        ligneRepo.delete(ligne);
        commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeVente modifierLigne(Long commandeId, Long ligneId, Integer quantite) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de modifier une commande terminée");
        }

        LigneCommandeVente ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée : " + ligneId));

        ligne.setQuantite(quantite);
        ligneRepo.save(ligne);

        return commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeVente changerStatut(Long commandeId, CommandeStatut nouveauStatut) {
        CommandeVente cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de changer le statut d'une commande terminée");
        }

        cmd.setStatut(nouveauStatut);
        return commandeRepo.save(cmd);
    }

    @Override
    public List<CommandeVente> search(CommandeVenteSearchCriteria criteria) {
        Specification<CommandeVente> spec = CommandeVenteSpecification.withCriteria(criteria);
        return commandeRepo.findAll(spec);
    }
}
