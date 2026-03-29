package com.bookstore.orderservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public Order createOrder(@RequestBody Order order,@ RequestHeader("Authorization") String authorization){
       
        return orderService.createOrder(order, authorization);
    }

    @GetMapping("/all")
    public List<Order> getAll(@RequestHeader("Authorization") String authorization){
        return orderService.getAllOrders();
    }
    @GetMapping("/my")
    public List<Order> getOrders(@ RequestHeader("Authorization") String authorization) {
     
        return orderService.getOrdersForUser(authorization);
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted");
    }
    
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable int id,
                             @RequestHeader("Authorization") String authorization) {

       
        return orderService.getOrderByIdForUser(id, authorization);
    }

    @GetMapping("/admin/{id}")
    public Order getOrderByIdAdmin(@PathVariable int id) {

        return orderService.getOrderById(id);
    }
}
