package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.CommandeAchat;
import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.model.entity.Produit;
import com.novaerp.demo.repository.CommandeAchatRepository;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.repository.ProduitRepository;
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
class CommandeAchatControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandeAchatRepository commandeAchatRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    private Fournisseur fournisseur;

    @BeforeEach
    void setUp() {
        commandeAchatRepository.deleteAll();
        produitRepository.deleteAll();
        fournisseurRepository.deleteAll();

        fournisseur = new Fournisseur();
        fournisseur.setNom("FournisseurTest");
        fournisseur.setCode("FOUR_TEST");
        fournisseur.setEmail("four@test.com");
        fournisseurRepository.save(fournisseur);
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testListCommandes() throws Exception {
        mockMvc.perform(get("/stock/commandes/achat"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/commandes/achat/list"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testNewCommandeForm() throws Exception {
        mockMvc.perform(get("/stock/commandes/achat/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/commandes/achat/form"))
                .andExpect(model().attributeExists("fournisseurs"));
    }


}
