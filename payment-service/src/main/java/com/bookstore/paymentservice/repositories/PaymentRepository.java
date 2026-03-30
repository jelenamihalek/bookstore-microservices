package com.bookstore.paymentservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.paymentservice.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	List<Payment> findByUserId(int userId);
	List<Payment> findByOrderId(int orderId);
}
