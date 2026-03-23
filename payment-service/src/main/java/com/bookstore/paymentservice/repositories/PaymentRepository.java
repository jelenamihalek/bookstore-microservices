package com.bookstore.paymentservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.paymentservice.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {


}
