package com.bookstore.orderservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.Util.exceptions.BookNotFoundException;
import com.bookstore.Util.exceptions.InsufficientStockException;
import com.bookstore.Util.exceptions.InvalidOrderException;
import com.bookstore.Util.exceptions.OrderAccessDeniedException;
import com.bookstore.Util.exceptions.OrderNotFoundException;
import com.bookstore.Util.exceptions.UserNotFoundByEmailException;
import com.bookstore.Util.exceptions.UserNotFoundException;
import com.bookstore.orderservice.clients.BookClient;
import com.bookstore.orderservice.clients.NotificationClient;
import com.bookstore.orderservice.clients.PaymentClient;
import com.bookstore.orderservice.clients.UserClient;
import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.repository.OrderRepository;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.BookDTO;
import com.bookstore.service_library.dtos.NotificationDTO;
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
	@Autowired
	private NotificationClient notificationClient;
	
	@Autowired
	private Decoder decoder;
	
	public OrderService(OrderRepository orderRepository, BookClient bookClient,UserClient userClient,
			PaymentClient paymentClient,
			NotificationClient notificationClient,
			Decoder decoder) {
		super();
		this.orderRepository = orderRepository;
		this.bookClient=bookClient;
		this.userClient=userClient;
		this.paymentClient=paymentClient;
		this.decoder=decoder;
		this.notificationClient=notificationClient;
	}
	
	public List<Order> getAllOrders(){
		return orderRepository.findAll();
	}
	 public Order getOrderById(int id) {
	        return orderRepository.findById(id)
	                .orElseThrow(() -> new OrderNotFoundException("Order not found",id));
	    }
	
	 public Order createOrder(Order order,String authorization) {

		 String email=decoder.decodeHeader(authorization);
		    // 1. uzmi user iz users-service
		    UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new UserNotFoundByEmailException("User not found",email);
		    }
		    
		    if (order.getQuantity() <= 0) {
		        throw new InvalidOrderException("Quantity must be greater than 0");
		    }

		    if (order.getBookId() <= 0) {
		        throw new InvalidOrderException("Invalid book ID");
		    }

		    // 🔥 KLJUČ: setuj userId iz auth-a
		    order.setUserId(user.getId());

		    // 2. book
		    BookDTO book = bookClient.getBookById(order.getBookId());
		    if (book == null) {
		        throw new BookNotFoundException("Book not found",order.getBookId());
		    }

		    if (book.getStock() < order.getQuantity()) {
		        throw new InsufficientStockException("Not enough stock",order.getQuantity(),book.getStock());
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
		            NotificationDTO dto = new NotificationDTO();
		            dto.setEmail(user.getEmail());
		            dto.setSubject("Order Confirmed");
		            dto.setMessage("Your order is CONFIRMED");

		            notificationClient.sendNotification(dto);
		        } else {
		            savedOrder.setStatus("FAILED");
		            NotificationDTO dto = new NotificationDTO();
		            dto.setEmail(user.getEmail());
		            dto.setSubject("Order Failed");
		            dto.setMessage("Your order FAILED");

		            notificationClient.sendNotification(dto);
		            bookClient.increaseStock(order.getBookId(), order.getQuantity());
		        }

		    } catch (Exception e) {
		        savedOrder.setStatus("FAILED");
		        NotificationDTO dto = new NotificationDTO();
		        dto.setEmail(user.getEmail());
		        dto.setSubject("Order Failed");
		        dto.setMessage("Payment service error - order FAILED");

		        notificationClient.sendNotification(dto);
		        bookClient.increaseStock(order.getBookId(), order.getQuantity());
		    }

		    return orderRepository.save(savedOrder);
		}
	 
	public Order updateOrder(int id, Order updatedOrder) {

	    Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found",id));

	    order.setBookId(updatedOrder.getBookId());
	    order.setUserId(updatedOrder.getUserId());
	    order.setQuantity(updatedOrder.getQuantity());
	  

	    return orderRepository.save(order);
	}
	
	public void deleteOrder(int id) {
		  Order order = orderRepository.findById(id)
	                .orElseThrow(() -> new OrderNotFoundException("Order not found",id));
		orderRepository.delete(order);
	}
	
	public List<Order> getOrdersForUser(String authorization) {
		 String email=decoder.decodeHeader(authorization);

		 UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new UserNotFoundByEmailException("User not found",email);
		    }

		    return orderRepository.findByUserId(user.getId());
	}
	
	public Order getOrderByIdForUser(int id, String authorization) {

		 String email=decoder.decodeHeader(authorization);
	     UserDTO user = userClient.findByEmail(email);

	    if (user == null) {
	        throw new UserNotFoundException("User not found", null);
	    }

	    Order order = orderRepository.findById(id)
	            .orElseThrow(() -> new OrderNotFoundException("Order not found", id));

	    if (order.getUserId() != (user.getId())) {
	        throw new OrderAccessDeniedException("You are not allowed to view this order",id);
	    }

	    return order;
	}
}
