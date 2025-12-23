package com.novaerp.demo.model.dto;

import com.novaerp.demo.model.typologie.FactureType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FactureSearchCriteria {
    private String numeroFacture;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;
    private Long commandeId;
    private FactureType type;
}