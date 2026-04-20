package com.bookstore.paymentservice.integration;

import com.bookstore.service_library.clients.UserClient;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.UserDTO;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private UserClient userClient;

    @org.springframework.boot.test.mock.mockito.MockBean
    private Decoder decoder;

    @Test
    void shouldGetPaymentsForUser() throws Exception {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail("mail")).thenReturn(user);

        mockMvc.perform(get("/payments")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }
}
