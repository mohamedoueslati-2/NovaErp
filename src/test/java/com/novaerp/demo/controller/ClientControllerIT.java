package com.novaerp.demo.controller;

import com.novaerp.demo.model.entity.Client;
import com.novaerp.demo.repository.ClientRepository;
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
class ClientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        Client client = new Client();
        client.setNom("ClientTest");
        client.setCode("CLT_TEST");
        client.setEmail("test@email.com"); // Ajout de l'email obligatoire
        clientRepository.save(client);
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testListClients() throws Exception {
        mockMvc.perform(get("/sales/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/clients/list"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testNewClientForm() throws Exception {
        mockMvc.perform(get("/sales/clients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/clients/form"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testEditClient_Success() throws Exception {
        Client client = clientRepository.findAll().get(0);
        mockMvc.perform(get("/sales/clients/edit/" + client.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/clients/form"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testEditClient_NotFound() throws Exception {
        mockMvc.perform(get("/sales/clients/edit/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/clients"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testDeleteClient_Success() throws Exception {
        Client client = clientRepository.findAll().get(0);
        mockMvc.perform(get("/sales/clients/delete/" + client.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/clients"));
    }

    @Test
    @WithMockUser(roles = "VENTE")
    void testDeleteClient_NotFound() throws Exception {
        mockMvc.perform(get("/sales/clients/delete/9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sales/clients"));
    }
}
