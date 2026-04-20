package com.bookstore.paymentservice.controller;

import com.bookstore.paymentservice.controllers.PaymentController;
import com.bookstore.paymentservice.models.Payment;
import com.bookstore.paymentservice.services.PaymentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

import java.util.List;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    // ✅ POST
    @Test
    void shouldProcessPayment() throws Exception {

        Payment payment = new Payment();
        payment.setAmount(100);

        when(paymentService.processPayment(any())).thenReturn(payment);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "amount": 100,
                        "userId": 1,
                        "orderId": 1
                    }
                """))
                .andExpect(status().isOk());
    }

    // ✅ GET my payments
    @Test
    void shouldGetPaymentsForUser() throws Exception {

        when(paymentService.getPaymentsForUser(any()))
                .thenReturn(List.of(new Payment()));

        mockMvc.perform(get("/payments")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    // ✅ GET by id
    @Test
    void shouldGetPaymentById() throws Exception {

        Payment payment = new Payment();

        when(paymentService.getPaymentById(eq(1), any()))
                .thenReturn(payment);

        mockMvc.perform(get("/payments/1")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    // ✅ DELETE
    @Test
    void shouldDeletePayment() throws Exception {

        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isOk());
    }
}