package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.model.entity.CommandeVente;
import com.novaerp.demo.repository.ClientRepository;
import com.novaerp.demo.repository.CommandeVenteRepository;
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
class CommandeVenteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CommandeVenteRepository commandeVenteRepository;

    private Client client;
    private CommandeVente commandeVente;

    @BeforeEach
    void setUp() {
        commandeVenteRepository.deleteAll();
        clientRepository.deleteAll();

        client = new Client();
        client.setNom("ClientTest");
        client.setCode("CLI_TEST");
        client.setEmail("cli@test.com");
        clientRepository.save(client);

        commandeVente = new CommandeVente();
        commandeVente.setClientId(client.getId());
        commandeVenteRepository.save(commandeVente);
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testListCommandes() throws Exception {
        mockMvc.perform(get("/sales/commandes"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/commandes/list"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testNewCommandeForm() throws Exception {
        mockMvc.perform(get("/sales/commandes/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/commandes/form"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testDetailCommande_Success() throws Exception {
        mockMvc.perform(get("/sales/commandes/" + commandeVente.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/commandes/detail"))
                .andExpect(model().attributeExists("commande"));
    }


}
