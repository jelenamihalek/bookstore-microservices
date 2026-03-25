package com.bookstore.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.orderservice.models.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{
	List<Order> findByUserId(int userId);
}
