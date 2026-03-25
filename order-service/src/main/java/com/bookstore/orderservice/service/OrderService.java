package com.bookstore.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.orderservice.clients.BookClient;
import com.bookstore.orderservice.clients.PaymentClient;
import com.bookstore.orderservice.clients.UserClient;
import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.repository.OrderRepository;
import com.bookstore.service_library.dtos.BookDTO;
import com.bookstore.service_library.dtos.PaymentDTO;
import com.bookstore.service_library.dtos.UserDTO;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private BookClient bookClient;
	
	@Autowired
	private UserClient userClient;
	
	@Autowired
	private PaymentClient paymentClient;

	public OrderService(OrderRepository orderRepository, BookClient bookClient,UserClient userClient,PaymentClient paymentClient) {
		super();
		this.orderRepository = orderRepository;
		this.bookClient=bookClient;
		this.userClient=userClient;
		this.paymentClient=paymentClient;
	}
	
	public List<Order> getAllOrders(){
		return orderRepository.findAll();
	}
	 public Order getOrderById(int id) {
	        return orderRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Order not found"));
	    }
	
	 public Order createOrder(Order order, String email) {

		    // 1. uzmi user iz users-service
		    UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new RuntimeException("User not found");
		    }

		    // 🔥 KLJUČ: setuj userId iz auth-a
		    order.setUserId(user.getId());

		    // 2. book
		    BookDTO book = bookClient.getBookById(order.getBookId());
		    if (book == null) {
		        throw new RuntimeException("Book not found");
		    }

		    if (book.getStock() < order.getQuantity()) {
		        throw new RuntimeException("Not enough stock");
		    }

		    // 3. smanji stock
		    bookClient.decreaseStock(order.getBookId(), order.getQuantity());

		    // 4. status
		    order.setStatus("PENDING");

		    // 5. save
		    Order savedOrder = orderRepository.save(order);

		    try {
		        // 6. payment
		        PaymentDTO payment = new PaymentDTO();
		        payment.setOrderId(savedOrder.getId());
		        payment.setAmount(book.getPrice() * order.getQuantity());

		        PaymentDTO response = paymentClient.processPayment(payment);

		        if (response != null && "SUCCESS".equals(response.getStatus())) {
		            savedOrder.setStatus("CONFIRMED");
		        } else {
		            savedOrder.setStatus("FAILED");
		            bookClient.increaseStock(order.getBookId(), order.getQuantity());
		        }

		    } catch (Exception e) {
		        savedOrder.setStatus("FAILED");
		        bookClient.increaseStock(order.getBookId(), order.getQuantity());
		    }

		    return orderRepository.save(savedOrder);
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
	
	public List<Order> getOrdersForUser(String email) {
		 UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new RuntimeException("User not found");
		    }

		    return orderRepository.findByUserId(user.getId());
	}
}
