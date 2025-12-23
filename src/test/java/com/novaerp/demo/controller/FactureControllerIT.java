package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Facture;
import com.novaerp.demo.model.typologie.FactureType;
import com.novaerp.demo.repository.FactureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FactureControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FactureRepository factureRepository;

    private Facture facture;

    @BeforeEach
    void setUp() {
        factureRepository.deleteAll();
        facture = new Facture();
        facture.setType(FactureType.FACTURE_FOURNISSEUR);
        facture.setStatut("NON_PAYEE");
        factureRepository.save(facture);
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testListFactures() throws Exception {
        mockMvc.perform(get("/stock/factures"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/factures/list"))
                .andExpect(model().attributeExists("factures"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testNewFactureForm() throws Exception {
        mockMvc.perform(get("/stock/factures/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/factures/form"))
                .andExpect(model().attributeExists("facture"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testDetailFacture_Success() throws Exception {
        mockMvc.perform(get("/stock/factures/detail/" + facture.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/factures/detail"))
                .andExpect(model().attributeExists("facture"));
    }


}
