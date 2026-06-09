package com.bookstore.orderservice.service;

import java.util.ArrayList;
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
import com.bookstore.orderservice.models.OrderItem;
import com.bookstore.orderservice.repository.OrderItemRepository;
import com.bookstore.orderservice.repository.OrderRepository;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.BookDTO;
import com.bookstore.service_library.dtos.OrderItemRequestDTO;
import com.bookstore.service_library.dtos.OrderItemResponseDTO;
import com.bookstore.service_library.dtos.OrderRequestDTO;
import com.bookstore.service_library.dtos.OrderResponseDTO;
import com.bookstore.service_library.dtos.PaymentDTO;
import com.bookstore.service_library.dtos.UserDTO;
import com.bookstore.service_library.events.OrderNotificationEvent;

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
	private EventPublisher eventPublisher;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private Decoder decoder;
	
	public OrderService(OrderRepository orderRepository, BookClient bookClient,UserClient userClient,
			PaymentClient paymentClient,
			NotificationClient notificationClient,
			Decoder decoder, EventPublisher eventPublisher,OrderItemRepository orderItemRepository) {
		super();
		this.orderRepository = orderRepository;
		this.bookClient=bookClient;
		this.userClient=userClient;
		this.paymentClient=paymentClient;
		this.decoder=decoder;
		this.notificationClient=notificationClient;
		this.eventPublisher = eventPublisher;
		this.orderItemRepository=orderItemRepository;
	
	}
	
	public List<Order> getAllOrders(){
		return orderRepository.findAll();
	}
	 public Order getOrderById(int id) {
	        return orderRepository.findById(id)
	                .orElseThrow(() -> new OrderNotFoundException("Order not found",id));
	 }
	 
	 public OrderResponseDTO createOrder( OrderRequestDTO request,String authorization) {

		    String email = decoder.decodeHeader(authorization);

		    UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new UserNotFoundByEmailException(
		                "User not found",
		                email
		        );
		    }

		    Order order = new Order();
		    order.setUserId(user.getId());
		    order.setStatus("PENDING");

		    Order savedOrder = orderRepository.save(order);

		    double totalAmount = 0;
		    List<OrderItemResponseDTO> responseItems = new ArrayList<>();
		    
		    for (OrderItemRequestDTO item : request.getItems()) {

		        if (item.getQuantity() <= 0) { 
		        	throw new InvalidOrderException("Quantity must be greater than 0");
		        }

		        if (item.getBookId() <= 0) {
		            throw new InvalidOrderException("Invalid book id");
		        }
		    }


		    try {

		        for (OrderItemRequestDTO item : request.getItems()) {

		           
		            BookDTO book =bookClient.getBookById(item.getBookId());
		       

		            if (book == null) {
		                throw new BookNotFoundException("Book not found",item.getBookId() );
		            }

		            if (book.getStock() < item.getQuantity()) {
		                throw new InsufficientStockException( "Not enough stock",item.getQuantity(),book.getStock());
		            }
		            
		            OrderItemResponseDTO responseItem =new OrderItemResponseDTO();

		            responseItem.setBookId(book.getId());
		            responseItem.setTitle(book.getTitle());
		            responseItem.setQuantity(item.getQuantity());

		            responseItems.add(responseItem);
		            bookClient.decreaseStock( item.getBookId(),item.getQuantity() );

		            totalAmount +=book.getPrice() * item.getQuantity();

		            OrderItem orderItem = new OrderItem();

		            orderItem.setOrderId(savedOrder.getId());

		            orderItem.setBookId(item.getBookId());

		            orderItem.setQuantity(item.getQuantity());

		            orderItemRepository.save(orderItem);
		        }

		        PaymentDTO payment =new PaymentDTO();

		        payment.setOrderId(savedOrder.getId());

		        payment.setUserId(user.getId());

		        payment.setAmount(totalAmount);

		        PaymentDTO response =paymentClient.processPayment(payment);

		        if (response != null &&"SUCCESS".equals(response.getStatus())) {

		        	savedOrder.setStatus("CONFIRMED");

		        	StringBuilder message = new StringBuilder();

		        	message.append("Your order has been confirmed.\n\n");

		        	message.append("Order ID: ").append(savedOrder.getId())
		        	       .append("\n\n");

		        	message.append("Books:\n");

		        	for (OrderItemResponseDTO item : responseItems) {

		        	    message.append("- ")
		        	           .append(item.getTitle())
		        	           .append(" x")
		        	           .append(item.getQuantity())
		        	           .append("\n");
		        	}

		        	message.append("\nTotal amount: ")
		        	       .append(totalAmount)
		        	       .append("\n");

		        	message.append("\nStatus: CONFIRMED");

		        	publishNotification( user.getEmail(),"Order Confirmed", message.toString()
		        	);

		        } else {

		            savedOrder.setStatus("FAILED");

		            publishNotification(user.getEmail(),"Order Failed","Your order FAILED");

		            rollbackStock(savedOrder.getId());
		        }

		    } catch (InvalidOrderException e) {
		        throw e;
		    }
		    catch (Exception e) {

		        savedOrder.setStatus("FAILED");

		        publishNotification(user.getEmail(),"Order Failed","Payment service error - order FAILED");

		        rollbackStock(savedOrder.getId());
		    }

		  
		    orderRepository.save(savedOrder);

		    OrderResponseDTO response =new OrderResponseDTO();

		    response.setOrderId(savedOrder.getId() );

		    response.setStatus(savedOrder.getStatus() );

		    response.setTotalAmount(totalAmount );

		    response.setItems(responseItems );

		    return response;
		}
	 private void rollbackStock(int orderId) {

		    List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

		    for (OrderItem item : items) {

		        bookClient.increaseStock( item.getBookId(),item.getQuantity());
		    }
		}

	 
	 private void publishNotification(String email,String subject,String message) {

		    eventPublisher.publish(new OrderNotificationEvent(email,subject,message)
		    );
		}
	
	public Order updateOrder(int id, Order updatedOrder) {

	    Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found",id));
	    //potrebno je dodati validacije

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
