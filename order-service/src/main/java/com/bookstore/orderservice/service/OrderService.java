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
import com.bookstore.orderservice.models.OrderItem;
import com.bookstore.orderservice.repository.OrderItemRepository;
import com.bookstore.orderservice.repository.OrderRepository;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.BookDTO;
import com.bookstore.service_library.dtos.NotificationDTO;
import com.bookstore.service_library.dtos.OrderItemRequestDTO;
import com.bookstore.service_library.dtos.OrderRequestDTO;
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
	 public Order createOrder(
		        OrderRequestDTO request,
		        String authorization) {
		 
		
		  System.out.println("USAO U CREATE ORDER");

		    System.out.println("REQUEST = " + request);

		    if(request.getItems() != null) {
		        for(OrderItemRequestDTO item : request.getItems()) {
		            System.out.println(
		                "BOOK=" + item.getBookId()
		                + " QUANTITY=" + item.getQuantity()
		            );
		        }
		    }
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
		    for (OrderItemRequestDTO item : request.getItems()) {

		        if (item.getQuantity() <= 0) {
		            throw new InvalidOrderException(
		                    "Quantity must be greater than 0"
		            );
		        }

		        if (item.getBookId() <= 0) {
		            throw new InvalidOrderException(
		                    "Invalid book id"
		            );
		        }
		    }


		    try {

		        for (OrderItemRequestDTO item : request.getItems()) {

		           
		            BookDTO book =
		                    bookClient.getBookById(
		                            item.getBookId()
		                    );

		            if (book == null) {
		                throw new BookNotFoundException(
		                        "Book not found",
		                        item.getBookId()
		                );
		            }

		            if (book.getStock() < item.getQuantity()) {
		                throw new InsufficientStockException(
		                        "Not enough stock",
		                        item.getQuantity(),
		                        book.getStock()
		                );
		            }

		            bookClient.decreaseStock(
		                    item.getBookId(),
		                    item.getQuantity()
		            );

		            totalAmount +=
		                    book.getPrice()
		                    * item.getQuantity();

		            OrderItem orderItem =
		                    new OrderItem();

		            orderItem.setOrderId(
		                    savedOrder.getId()
		            );

		            orderItem.setBookId(
		                    item.getBookId()
		            );

		            orderItem.setQuantity(
		                    item.getQuantity()
		            );

		            orderItemRepository.save(
		                    orderItem
		            );
		        }

		        PaymentDTO payment =
		                new PaymentDTO();

		        payment.setOrderId(
		                savedOrder.getId()
		        );

		        payment.setUserId(
		                user.getId()
		        );

		        payment.setAmount(
		                totalAmount
		        );

		        PaymentDTO response =
		                paymentClient.processPayment(
		                        payment
		                );

		        if (response != null &&
		                "SUCCESS".equals(
		                        response.getStatus())) {

		            savedOrder.setStatus(
		                    "CONFIRMED"
		            );

		            publishNotification(
		                    user.getEmail(),
		                    "Order Confirmed",
		                    "Your order is CONFIRMED"
		            );

		        } else {

		            savedOrder.setStatus(
		                    "FAILED"
		            );

		            publishNotification(
		                    user.getEmail(),
		                    "Order Failed",
		                    "Your order FAILED"
		            );

		            rollbackStock(
		                    savedOrder.getId()
		            );
		        }

		    } catch (InvalidOrderException e) {
		        throw e;
		    }
		    catch (Exception e) {

		        savedOrder.setStatus(
		                "FAILED"
		        );

		        publishNotification(
		                user.getEmail(),
		                "Order Failed",
		                "Payment service error - order FAILED"
		        );

		        rollbackStock(
		                savedOrder.getId()
		        );
		    }

		    return orderRepository.save(
		            savedOrder
		    );
		}
	 private void rollbackStock(int orderId) {

		    List<OrderItem> items =
		            orderItemRepository
		                    .findByOrderId(orderId);

		    for (OrderItem item : items) {

		        bookClient.increaseStock(
		                item.getBookId(),
		                item.getQuantity()
		        );
		    }
		}
	/* public Order createOrder(Order order, String authorization) {

		    String email = decoder.decodeHeader(authorization);

		    // 1. User
		    UserDTO user = userClient.findByEmail(email);

		    if (user == null) {
		        throw new UserNotFoundByEmailException(
		                "User not found",
		                email
		        );
		    }

		    if (order.getQuantity() <= 0) {
		        throw new InvalidOrderException(
		                "Quantity must be greater than 0"
		        );
		    }

		    if (order.getBookId() <= 0) {
		        throw new InvalidOrderException(
		                "Invalid book ID"
		        );
		    }

		    order.setUserId(user.getId());

		    // 2. Book
		    BookDTO book = bookClient.getBookById(
		            order.getBookId()
		    );

		    if (book == null) {
		        throw new BookNotFoundException(
		                "Book not found",
		                order.getBookId()
		        );
		    }

		    if (book.getStock() < order.getQuantity()) {
		        throw new InsufficientStockException(
		                "Not enough stock",
		                order.getQuantity(),
		                book.getStock()
		        );
		    }

		    // 3. Decrease stock
		    bookClient.decreaseStock(
		            order.getBookId(),
		            order.getQuantity()
		    );

		    // 4. Initial status
		    order.setStatus("PENDING");

		    // 5. Save order
		    Order savedOrder = orderRepository.save(order);

		    try {

		        // 6. Payment
		        PaymentDTO payment = new PaymentDTO();
		        payment.setOrderId(savedOrder.getId());
		        payment.setAmount(
		                book.getPrice() * order.getQuantity()
		        );
		        payment.setUserId(user.getId());

		        PaymentDTO response =
		                paymentClient.processPayment(payment);

		        if (response != null &&
		                "SUCCESS".equals(response.getStatus())) {

		            savedOrder.setStatus("CONFIRMED");

		            publishNotification(
		                    user.getEmail(),
		                    "Order Confirmed",
		                    "Your order is CONFIRMED"
		            );

		        } else {

		            savedOrder.setStatus("FAILED");

		            publishNotification(
		                    user.getEmail(),
		                    "Order Failed",
		                    "Your order FAILED"
		            );

		            bookClient.increaseStock(
		                    order.getBookId(),
		                    order.getQuantity()
		            );
		        }

		    } catch (Exception e) {

		        savedOrder.setStatus("FAILED");

		        publishNotification(
		                user.getEmail(),
		                "Order Failed",
		                "Payment service error - order FAILED"
		        );

		        bookClient.increaseStock(
		                order.getBookId(),
		                order.getQuantity()
		        );
		    }

		    return orderRepository.save(savedOrder);
		}*/
	 
	 private void publishNotification(
		        String email,
		        String subject,
		        String message) {

		    eventPublisher.publish(
		            new OrderNotificationEvent(
		                    email,
		                    subject,
		                    message
		            )
		    );
		}
	/* public Order createOrder(Order order,String authorization) {

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
		        payment.setUserId(user.getId());

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
		}*/
	 
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
