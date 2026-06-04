package com.bookstore.orderservice.service;

import com.bookstore.orderservice.models.Order;
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

    // ✅ SUCCESS FLOW
    @Test
    void shouldCreateOrderSuccessfully() {

        Order order = new Order();
        order.setBookId(1);
        order.setQuantity(2);

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

        Order result = orderService.createOrder(order, "token");

        assertEquals("CONFIRMED", result.getStatus());
       // verify(notificationClient).sendNotification(any());
        verify(eventPublisher)
        .publish(any());
    }

    // ❌ USER NOT FOUND
    @Test
    void shouldThrow_whenUserNotFound() {
        when(decoder.decodeHeader(any())).thenReturn("mail");
        when(userClient.findByEmail(any())).thenReturn(null);

        Order order = new Order();

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(order, "token"));
    }

    // ❌ INVALID QUANTITY
    @Test
    void shouldThrow_whenQuantityInvalid() {
        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

        Order order = new Order();
        order.setQuantity(0);

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(order, "token"));
    }

    // ❌ NOT ENOUGH STOCK
    @Test
    void shouldThrow_whenNotEnoughStock() {
        when(decoder.decodeHeader(any())).thenReturn("mail");

        UserDTO user = new UserDTO();
        user.setId(1);

        when(userClient.findByEmail(any())).thenReturn(user);

        Order order = new Order();
        order.setBookId(1);
        order.setQuantity(10);

        BookDTO book = new BookDTO();
        book.setStock(5);

        when(bookClient.getBookById(1)).thenReturn(book);

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(order, "token"));
    }

    // ❌ PAYMENT FAIL
    @Test
    void shouldSetFailed_whenPaymentFails() {

        Order order = new Order();
        order.setBookId(1);
        order.setQuantity(1);

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

        PaymentDTO response = new PaymentDTO();
        response.setStatus("FAILED");

        when(paymentClient.processPayment(any())).thenReturn(response);

        Order result = orderService.createOrder(order, "token");

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

        Order order = new Order();
        order.setBookId(1);
        order.setQuantity(1);

        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(order, "token"));
    }
    @Test
    void shouldHandleException_whenPaymentServiceFails() {

        Order order = new Order();
        order.setBookId(1);
        order.setQuantity(1);

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

        when(paymentClient.processPayment(any()))
                .thenThrow(new RuntimeException());

        Order result = orderService.createOrder(order, "token");

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