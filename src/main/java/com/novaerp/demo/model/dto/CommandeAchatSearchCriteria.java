package com.novaerp.demo.model.dto;

import com.novaerp.demo.model.typologie.CommandeStatut;
import java.time.LocalDate;

public class CommandeAchatSearchCriteria {
    private Long fournisseurId;
    private CommandeStatut statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long factureId;

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public CommandeStatut getStatut() {
        return statut;
    }

    public void setStatut(CommandeStatut statut) {
        this.statut = statut;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Long getFactureId() {
        return factureId;
    }

    public void setFactureId(Long factureId) {
        this.factureId = factureId;
    }
}
