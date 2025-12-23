package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Produit;
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
class ProduitControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    private Produit produit;

    @BeforeEach
    void setUp() {
        produitRepository.deleteAll();
        produit = new Produit();
        produit.setNom("ProduitTest");
        produit.setReference("REF_TEST");
        produitRepository.save(produit);
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testListProduits() throws Exception {
        mockMvc.perform(get("/stock/produits"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/produits/list"))
                .andExpect(model().attributeExists("produits"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testNewProduitForm() throws Exception {
        mockMvc.perform(get("/stock/produits/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/produits/form"))
                .andExpect(model().attributeExists("produit"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testViewProduit_Success() throws Exception {
        mockMvc.perform(get("/stock/produits/" + produit.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/produits/view"))
                .andExpect(model().attributeExists("produit"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testViewProduit_NotFound() throws Exception {
        mockMvc.perform(get("/stock/produits/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/produits"));
    }
}
