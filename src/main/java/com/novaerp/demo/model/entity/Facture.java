package com.novaerp.demo.model.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaerp.demo.model.typologie.FactureType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactureFournisseur;

    private LocalDate dateFacture;

    private Long commandeId;

    private Double montantTTC;

    private String statut; // PAYEE / NON_PAYEE

    @Enumerated(EnumType.STRING)
    @Column
    private FactureType type; // FACTURE_CLIENT / FACTURE_FOURNISSEUR

    @Column(columnDefinition = "json")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> extraAttributes = new HashMap<>();

    // Champ transient pour le formulaire
    @Transient
    private String extraAttributesJson;

    public void addExtra(String key, Object value) {
        if (extraAttributes == null) {
            extraAttributes = new HashMap<>();
        }
        extraAttributes.put(key, value);
    }

    // Getter pour affichage JSON
    public String getExtraAttributesJson() {
        if (extraAttributes == null || extraAttributes.isEmpty()) {
            return "{}";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(extraAttributes);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    // Setter pour récupération du formulaire
    public void setExtraAttributesJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            this.extraAttributes = new HashMap<>();
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.extraAttributes = mapper.readValue(json, Map.class);
        } catch (Exception e) {
            this.extraAttributes = new HashMap<>();
        }
    }
}