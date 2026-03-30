package com.bookstore.paymentservice.controllers;

import com.bookstore.paymentservice.models.Payment;
import com.bookstore.paymentservice.services.PaymentService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
    	this.paymentService=paymentService;
    }
    
    @PostMapping
    public Payment process(@RequestBody Payment payment) {
        return paymentService.processPayment(payment);
    }
    
    @GetMapping
    public List<Payment> getMyPayments(@RequestHeader("Authorization") String authorization) {
        return paymentService.getPaymentsForUser(authorization);
    }
    @GetMapping("/{id}")
    public Payment getById(@PathVariable int id,
                           @RequestHeader("Authorization") String authorization) {

        return paymentService.getPaymentById(id, authorization);
    }
    @GetMapping("/admin")
    public List<Payment> getAll() {
        return paymentService.getAllPayments();
    }
    @PutMapping("/{id}")
    public Payment update(@PathVariable int id, @RequestBody Payment payment) {
        return paymentService.updatePayment(id, payment);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        paymentService.deletePayment(id);
    }
}