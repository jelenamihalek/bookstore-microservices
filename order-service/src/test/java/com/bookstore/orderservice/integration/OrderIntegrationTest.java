package com.bookstore.orderservice.integration;

import com.bookstore.orderservice.clients.*;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserClient userClient;
    @MockBean private BookClient bookClient;
    @MockBean private PaymentClient paymentClient;
    @MockBean private NotificationClient notificationClient;
    @MockBean private Decoder decoder;

    // ✅ FULL FLOW SUCCESS
    @Test
    void shouldCreateOrder_endToEnd_success() throws Exception {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setEmail("mail");

        when(userClient.findByEmail(any())).thenReturn(user);

        BookDTO book = new BookDTO();
        book.setStock(10);
        book.setPrice(50);

        when(bookClient.getBookById(1)).thenReturn(book);

        PaymentDTO payment = new PaymentDTO();
        payment.setStatus("SUCCESS");

        when(paymentClient.processPayment(any())).thenReturn(payment);

        mockMvc.perform(post("/orders")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "bookId": 1,
                        "quantity": 2
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    // ❌ PAYMENT FAIL FLOW
    @Test
    void shouldCreateOrder_failedPayment() throws Exception {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setEmail("mail");

        when(userClient.findByEmail(any())).thenReturn(user);

        BookDTO book = new BookDTO();
        book.setStock(10);
        book.setPrice(50);

        when(bookClient.getBookById(1)).thenReturn(book);

        PaymentDTO payment = new PaymentDTO();
        payment.setStatus("FAILED");

        when(paymentClient.processPayment(any())).thenReturn(payment);

        mockMvc.perform(post("/orders")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "bookId": 1,
                        "quantity": 2
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));
    }

    // ❌ INVALID QUANTITY
    @Test
    void shouldFail_whenInvalidQuantity() throws Exception {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

        mockMvc.perform(post("/orders")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "bookId": 1,
                        "quantity": 0
                    }
                """))
                .andExpect(status().is4xxClientError());
    }
}