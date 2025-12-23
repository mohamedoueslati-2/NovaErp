package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.dto.CommandeAchatSearchCriteria;
import com.novaerp.demo.model.entity.CommandeAchat;
import com.novaerp.demo.model.entity.LigneCommandeAchat;
import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.model.typologie.CommandeStatut;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.CommandeAchatRepository;
import com.novaerp.demo.repository.CommandeAchatSpecification;
import com.novaerp.demo.repository.FactureRepository;
import com.novaerp.demo.repository.LigneCommandeAchatRepository;
import com.novaerp.demo.repository.ProduitRepository;
import com.novaerp.demo.service.CommandeAchatService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CommandeAchatServiceImpl implements CommandeAchatService {

    @Autowired
    private CommandeAchatRepository commandeRepo;

    @Autowired
    private LigneCommandeAchatRepository ligneRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @Autowired
    private FactureRepository factureRepo;

    @Override
    @Transactional
    public CommandeAchat creerCommande(Long fournisseurId) {
        CommandeAchat c = new CommandeAchat();
        c.setFournisseurId(fournisseurId);
        c.setStatut(CommandeStatut.EN_COURS);
        return commandeRepo.save(c);
    }

    @Override
    @Transactional
    public CommandeAchat ajouterLigne(Long commandeId, Long produitId, Integer quantite, Double prixAchat) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setProduitId(produitId);
        ligne.setQuantite(quantite);
        ligne.setPrixAchat(prixAchat);

        cmd.addLigne(ligne);

        ligneRepo.save(ligne);
        return commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeAchat terminerCommande(Long commandeId) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        for (LigneCommandeAchat l : cmd.getLignes()) {
            Produit p = produitRepo.findById(l.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + l.getProduitId()));

            Integer stockActuel = p.getQuantite() == null ? 0 : p.getQuantite();
            p.setQuantite(stockActuel + l.getQuantite());

            BigDecimal ancienPrixBD = p.getPrixAchatParDefaut();
            Double prixLigne = l.getPrixAchat();

            if (prixLigne != null) {
                if (ancienPrixBD == null) {
                    p.setPrixAchatParDefaut(BigDecimal.valueOf(prixLigne));
                } else {
                    double ancienPrix = ancienPrixBD.doubleValue();
                    double prixMoyen = (ancienPrix + prixLigne) / 2.0;
                    p.setPrixAchatParDefaut(BigDecimal.valueOf(prixMoyen));
                }
            }

            produitRepo.save(p);
        }

        Facture facture = new Facture();
        facture.setDateFacture(LocalDate.now());
        facture.setCommandeId(cmd.getId());
        facture.setMontantTTC(cmd.getTotal());
        facture.setType(FactureType.FACTURE_FOURNISSEUR);
        facture.setStatut("NON_PAYEE");
        facture = factureRepo.save(facture);

        cmd.setFactureId(facture.getId());
        cmd.setStatut(CommandeStatut.TERMINEE);

        return commandeRepo.save(cmd);
    }

    @Override
    public List<CommandeAchat> findAll() {
        return commandeRepo.findAll();
    }

    @Override
    public CommandeAchat findById(Long id) {
        return commandeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + id));
    }

    @Override
    @Transactional
    public void supprimerCommande(Long commandeId) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        commandeRepo.delete(cmd);
    }

    @Override
    @Transactional
    public void supprimerLigne(Long commandeId, Long ligneId) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de modifier une commande terminée");
        }

        LigneCommandeAchat ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée : " + ligneId));

        cmd.getLignes().remove(ligne);
        ligneRepo.delete(ligne);
        commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeAchat modifierLigne(Long commandeId, Long ligneId, Integer quantite, Double prixAchat) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de modifier une commande terminée");
        }

        LigneCommandeAchat ligne = ligneRepo.findById(ligneId)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée : " + ligneId));

        ligne.setQuantite(quantite);
        ligne.setPrixAchat(prixAchat);

        ligneRepo.save(ligne);
        return commandeRepo.save(cmd);
    }

    @Override
    @Transactional
    public CommandeAchat changerStatut(Long commandeId, CommandeStatut nouveauStatut) {
        CommandeAchat cmd = commandeRepo.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + commandeId));

        if (cmd.getStatut() == CommandeStatut.TERMINEE) {
            throw new RuntimeException("Impossible de changer le statut d'une commande terminée");
        }

        cmd.setStatut(nouveauStatut);
        return commandeRepo.save(cmd);
    }

    @Override
    public List<CommandeAchat> searchCommandes(CommandeAchatSearchCriteria criteria) {
        if (criteria == null) {
            return commandeRepo.findAll();
        }
        return commandeRepo.findAll(CommandeAchatSpecification.withCriteria(criteria));
    }
}
