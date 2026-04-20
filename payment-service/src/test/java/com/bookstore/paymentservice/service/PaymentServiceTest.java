package com.bookstore.paymentservice.service;

import com.bookstore.paymentservice.models.Payment;
import com.bookstore.paymentservice.repositories.PaymentRepository;
import com.bookstore.paymentservice.services.PaymentService;
import com.bookstore.service_library.clients.UserClient;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.UserDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Decoder decoder;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PaymentService paymentService;

    // ✅ GET PAYMENTS FOR USER
    @Test
    void shouldReturnPaymentsForUser() {
        String token = "Bearer xyz";
        String email = "test@gmail.com";

        UserDTO user = new UserDTO();
        user.setId(1);

        Payment payment = new Payment();
        payment.setUserId(1);

        when(decoder.decodeHeader(token)).thenReturn(email);
        when(userClient.findByEmail(email)).thenReturn(user);
        when(paymentRepository.findByUserId(1)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getPaymentsForUser(token);

        assertEquals(1, result.size());
    }

    // ❌ USER NOT FOUND
    @Test
    void shouldThrow_whenUserNotFound() {
        when(decoder.decodeHeader(any())).thenReturn("mail");
        when(userClient.findByEmail(any())).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentsForUser("token"));
    }

    // ✅ GET PAYMENT BY ID
    @Test
    void shouldReturnPaymentById() {
        String token = "t";
        String email = "mail";

        UserDTO user = new UserDTO();
        user.setId(1);

        Payment payment = new Payment();
        payment.setId(1);
        payment.setUserId(1);

        when(decoder.decodeHeader(token)).thenReturn(email);
        when(userClient.findByEmail(email)).thenReturn(user);
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(1, token);

        assertEquals(1, result.getId());
    }

    // ❌ ACCESS DENIED
    @Test
    void shouldThrow_whenAccessDenied() {
        UserDTO user = new UserDTO();
        user.setId(1);

        Payment payment = new Payment();
        payment.setUserId(2);

        when(decoder.decodeHeader(any())).thenReturn("mail");
        when(userClient.findByEmail(any())).thenReturn(user);
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentById(1, "token"));
    }

    // ❌ PAYMENT NOT FOUND
    @Test
    void shouldThrow_whenPaymentNotFound() {
        UserDTO user = new UserDTO();
        user.setId(1);

        when(decoder.decodeHeader(any())).thenReturn("mail");
        when(userClient.findByEmail(any())).thenReturn(user);
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentById(1, "token"));
    }

    // ✅ UPDATE
    @Test
    void shouldUpdatePayment() {
        Payment existing = new Payment();
        existing.setId(1);

        Payment updated = new Payment();
        updated.setStatus("SUCCESS");

        when(paymentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any())).thenReturn(existing);

        Payment result = paymentService.updatePayment(1, updated);

        assertEquals("SUCCESS", result.getStatus());
    }

    // ✅ DELETE
    @Test
    void shouldDeletePayment() {
        Payment payment = new Payment();
        payment.setId(1);

        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(1);

        verify(paymentRepository).delete(payment);
    }
}