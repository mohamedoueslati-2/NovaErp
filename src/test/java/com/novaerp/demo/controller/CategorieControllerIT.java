package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Categorie;
import com.novaerp.demo.repository.CategorieRepository;
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
class CategorieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @BeforeEach
    void setUp() {
        produitRepository.deleteAll();
        categorieRepository.deleteAll();
        Categorie cat = new Categorie();
        cat.setNom("CatégorieTest");
        cat.setCode("CAT_TEST"); // Correction : code obligatoire
        categorieRepository.save(cat);
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testListCategories() throws Exception {
        mockMvc.perform(get("/stock/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/categories/list"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testNewCategorieForm() throws Exception {
        mockMvc.perform(get("/stock/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/categories/form"))
                .andExpect(model().attributeExists("categorie"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testEditCategorie_Success() throws Exception {
        Categorie cat = categorieRepository.findAll().get(0);
        mockMvc.perform(get("/stock/categories/edit/" + cat.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("stock/categories/form"))
                .andExpect(model().attributeExists("categorie"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testEditCategorie_NotFound() throws Exception {
        mockMvc.perform(get("/stock/categories/edit/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/categories"));
    }


    @Test
    @WithMockUser(roles = "STOCK")
    void testViewCategorie_NotFound() throws Exception {
        mockMvc.perform(get("/stock/categories/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/categories"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testDeleteCategorie_Success() throws Exception {
        Categorie cat = categorieRepository.findAll().get(0);
        mockMvc.perform(get("/stock/categories/delete/" + cat.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/categories"));
    }

    @Test
    @WithMockUser(roles = "STOCK")
    void testDeleteCategorie_NotFound() throws Exception {
        mockMvc.perform(get("/stock/categories/delete/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock/categories"));
    }
}
