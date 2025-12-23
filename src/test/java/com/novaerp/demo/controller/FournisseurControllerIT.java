package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.repository.FournisseurRepository;
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
class FournisseurControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    private Fournisseur fournisseur;

    @BeforeEach
    void setUp() {
        fournisseurRepository.deleteAll();
        fournisseur = new Fournisseur();
        fournisseur.setNom("FournisseurTest");
        fournisseur.setCode("FOU_TEST");
        fournisseur.setEmail("fou@test.com");
        fournisseurRepository.save(fournisseur);
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testListFournisseurs() throws Exception {
        mockMvc.perform(get("/stock/fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/fournisseurs/list"))
                .andExpect(model().attributeExists("fournisseurs"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testNewFournisseurForm() throws Exception {
        mockMvc.perform(get("/stock/fournisseurs/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/fournisseurs/form"))
                .andExpect(model().attributeExists("fournisseur"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testEditFournisseur_Success() throws Exception {
        mockMvc.perform(get("/stock/fournisseurs/edit/" + fournisseur.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/fournisseurs/form"))
                .andExpect(model().attributeExists("fournisseur"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testEditFournisseur_NotFound() throws Exception {
        mockMvc.perform(get("/stock/fournisseurs/edit/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/fournisseurs"));
    }
}
