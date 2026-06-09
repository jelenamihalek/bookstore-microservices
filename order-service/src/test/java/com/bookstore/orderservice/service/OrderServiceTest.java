package com.bookstore.orderservice.service;

import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.models.OrderItem;
import com.bookstore.orderservice.repository.OrderItemRepository;
import com.bookstore.orderservice.repository.OrderRepository;
import com.bookstore.orderservice.service.OrderService;
import com.bookstore.orderservice.clients.*;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private BookClient bookClient;
    @Mock private UserClient userClient;
    @Mock private PaymentClient paymentClient;
    @Mock private NotificationClient notificationClient;
    @Mock private Decoder decoder;
    @Mock private EventPublisher eventPublisher;
    @InjectMocks
    private OrderService orderService;
    
    @Mock
    private OrderItemRepository orderItemRepository;

    // SUCCESS FLOW
    @Test
    void shouldCreateOrderSuccessfully() {

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setBookId(1);
    	item.setQuantity(2);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setEmail("mail");

        when(userClient.findByEmail("mail")).thenReturn(user);

        BookDTO book = new BookDTO();
        book.setStock(10);
        book.setPrice(50);

        when(bookClient.getBookById(1)).thenReturn(book);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentDTO paymentResponse = new PaymentDTO();
        paymentResponse.setStatus("SUCCESS");

        when(paymentClient.processPayment(any())).thenReturn(paymentResponse);

        Order result = orderService.createOrder(request, "token");

        assertEquals("CONFIRMED", result.getStatus());
       // verify(notificationClient).sendNotification(any());
        verify(eventPublisher)
        .publish(any());
    }

    //  USER NOT FOUND
    @Test
    void shouldThrow_whenUserNotFound() {
        when(decoder.decodeHeader(any())).thenReturn("mail");
        when(userClient.findByEmail(any())).thenReturn(null);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
       

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(item));
        

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "token"));
    }

    // ❌ INVALID QUANTITY
    @Test
    void shouldThrow_whenQuantityInvalid() {
        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setQuantity(0);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));
        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "token"));
    }

    // NOT ENOUGH STOCK
    @Test
    void shouldThrow_whenNotEnoughStock() {
        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setBookId(1);
    	item.setQuantity(10);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));

        BookDTO book = new BookDTO();
        book.setStock(5);

        when(bookClient.getBookById(1)).thenReturn(book);

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "token"));
    }

    //  PAYMENT FAIL
    @Test
    void shouldSetFailed_whenPaymentFails() {

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setBookId(1);
    	item.setQuantity(1);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setEmail("mail");

        when(userClient.findByEmail(any())).thenReturn(user);

        BookDTO book = new BookDTO();
        book.setStock(10);
        book.setPrice(50);

        when(bookClient.getBookById(1)).thenReturn(book);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        OrderItem savedItem = new OrderItem();
        savedItem.setBookId(1);
        savedItem.setQuantity(1);

        when(orderItemRepository.findByOrderId(anyInt()))
                .thenReturn(List.of(savedItem));

        PaymentDTO response = new PaymentDTO();
        response.setStatus("FAILED");

        when(paymentClient.processPayment(any())).thenReturn(response);

        Order result = orderService.createOrder(request, "token");

        assertEquals("FAILED", result.getStatus());
        verify(bookClient).increaseStock(anyInt(), anyInt());
    }
    
    @Test
    void shouldThrow_whenBookNotFound() {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);
        when(bookClient.getBookById(anyInt())).thenReturn(null);

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setBookId(1);
    	item.setQuantity(1);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request, "token"));
    }
    @Test
    void shouldHandleException_whenPaymentServiceFails() {

    	OrderItemRequestDTO item = new OrderItemRequestDTO();
    	item.setBookId(1);
    	item.setQuantity(1);

    	OrderRequestDTO request = new OrderRequestDTO();
    	request.setItems(List.of(item));

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);
        user.setEmail("mail");

        when(userClient.findByEmail(any())).thenReturn(user);

        BookDTO book = new BookDTO();
        book.setStock(10);
        book.setPrice(50);

        when(bookClient.getBookById(anyInt())).thenReturn(book);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        OrderItem savedItem = new OrderItem();
        savedItem.setBookId(1);
        savedItem.setQuantity(1);

        when(orderItemRepository.findByOrderId(anyInt()))
                .thenReturn(List.of(savedItem));

        when(paymentClient.processPayment(any()))
                .thenThrow(new RuntimeException());

        Order result = orderService.createOrder(request, "token");

        assertEquals("FAILED", result.getStatus());
        verify(bookClient).increaseStock(anyInt(), anyInt());
    }
    @Test
    void shouldThrow_whenAccessDenied() {

        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

        Order order = new Order();
        order.setUserId(2);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> orderService.getOrderByIdForUser(1, "token"));
    }
}