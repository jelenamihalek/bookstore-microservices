package com.bookstore.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.orderservice.clients.BookClient;
import com.bookstore.orderservice.clients.UserClient;
import com.bookstore.orderservice.dtos.BookDTO;
import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private BookClient bookClient;
	
	@Autowired
	private UserClient userClient;

	public OrderService(OrderRepository orderRepository, BookClient bookClient,UserClient userClient) {
		super();
		this.orderRepository = orderRepository;
		this.bookClient=bookClient;
		this.userClient=userClient;
	}
	
	public List<Order> getAllOrders(){
		return orderRepository.findAll();
	}
	 public Order getOrderById(int id) {
	        return orderRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Order not found"));
	    }
	
	 public Order createOrder(Order order) {

	        Object user = userClient.getUserById(order.getUserId());
	        if (user == null) {
	            throw new RuntimeException("User not found");
	        }
	        
		    BookDTO book = bookClient.getBookById(order.getBookId());

		    if (book == null) {
		        throw new RuntimeException("Book not found");
		    }

		    if (book.getStock() < order.getQuantity()) {
		        throw new RuntimeException("Not enough stock");
		    }

		    bookClient.decreaseStock(order.getBookId(), order.getQuantity());

		    return orderRepository.save(order);
		}
	public Order updateOrder(int id, Order updatedOrder) {

	    Order order = orderRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    order.setBookId(updatedOrder.getBookId());
	    order.setUserId(updatedOrder.getUserId());
	    order.setQuantity(updatedOrder.getQuantity());
	  

	    return orderRepository.save(order);
	}
	
	public void deleteOrder(int id) {
		  Order order = orderRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Order not found"));
		orderRepository.delete(order);
	}
}
