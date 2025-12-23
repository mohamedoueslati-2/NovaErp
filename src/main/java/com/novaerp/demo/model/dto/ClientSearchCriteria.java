package com.novaerp.demo.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientSearchCriteria {
    private String code;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
}
