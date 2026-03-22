package com.bookstore.orderservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
    private  OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order){
        return orderService.createOrder(order);
    }

    @GetMapping
    public List<Order> getAll(){
        return orderService.getAllOrders();
    }
}
