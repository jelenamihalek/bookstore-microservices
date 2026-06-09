package com.bookstore.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.orderservice.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {

	List<OrderItem> findByOrderId(int orderId);
}
