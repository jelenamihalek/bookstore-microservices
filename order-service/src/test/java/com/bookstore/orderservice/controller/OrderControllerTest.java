package com.bookstore.orderservice.controller;

import com.bookstore.orderservice.controllers.OrderController;
import com.bookstore.orderservice.models.Order;
import com.bookstore.orderservice.service.EventPublisher;
import com.bookstore.orderservice.service.OrderService;
import com.bookstore.service_library.dtos.OrderItemResponseDTO;
import com.bookstore.service_library.dtos.OrderResponseDTO;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;
    
    @MockBean
    private EventPublisher eventPublisher;

    //  CREATE ORDER
    @Test
    void shouldCreateOrder() throws Exception {

    	 OrderItemResponseDTO item =new OrderItemResponseDTO();

    	    item.setBookId(1);
    	    item.setTitle("Test Book");
    	    item.setQuantity(2);

    	    OrderResponseDTO response = new OrderResponseDTO();

    	    response.setOrderId(1);
    	    response.setStatus("CONFIRMED");
    	    response.setTotalAmount(100);

    	    response.setItems(List.of(item)  );

    	    when(orderService.createOrder(any(), any())).thenReturn(response);

    	    mockMvc.perform(post("/orders")
    	            .header("Authorization", "Bearer test")
    	            .contentType(MediaType.APPLICATION_JSON)
    	            .content("""
    	                    {
    	                      "items": [
    	                        {
    	                          "bookId": 1,
    	                          "quantity": 2
    	                        }
    	                      ]
    	                    }
    	                    """))
    	            .andExpect(status().isOk())
    	            .andExpect(jsonPath("$.orderId").value(1))
    	            .andExpect(jsonPath("$.status").value("CONFIRMED"))
    	            .andExpect(jsonPath("$.items[0].bookId").value(1))
    	            .andExpect(jsonPath("$.items[0].title").value("Test Book"))
    	            .andExpect(jsonPath("$.items[0].quantity").value(2))
    	            .andExpect(jsonPath("$.totalAmount").value(100.0));
    }

    //  GET MY ORDERS
    @Test
    void shouldGetMyOrders() throws Exception {

        when(orderService.getOrdersForUser(any()))
                .thenReturn(List.of(new Order()));

        mockMvc.perform(get("/orders/my")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    //  GET ALL (ADMIN)
    @Test
    void shouldGetAllOrders() throws Exception {

        when(orderService.getAllOrders())
                .thenReturn(List.of(new Order()));

        mockMvc.perform(get("/orders/all")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    // ✅ DELETE
    @Test
    void shouldDeleteOrder() throws Exception {

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted"));
    }

    //  GET BY ID (USER)
    @Test
    void shouldGetOrderByIdForUser() throws Exception {

        Order order = new Order();
        order.setId(1);

        when(orderService.getOrderByIdForUser(anyInt(), any()))
                .thenReturn(order);

        mockMvc.perform(get("/orders/1")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID (ADMIN)
    @Test
    void shouldGetOrderByIdAdmin() throws Exception {

        Order order = new Order();
        order.setId(1);

        when(orderService.getOrderById(1)).thenReturn(order);

        mockMvc.perform(get("/orders/admin/1"))
                .andExpect(status().isOk());
    }
}