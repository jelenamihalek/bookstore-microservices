package com.bookstore.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.orderservice.models.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{

}
