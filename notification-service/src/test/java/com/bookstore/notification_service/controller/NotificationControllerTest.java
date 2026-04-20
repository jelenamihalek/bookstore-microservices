package com.bookstore.notification_service.controller;

import com.bookstore.notification_service.controllers.NotificationController;
import com.bookstore.notification_service.services.NotificationService;
import com.bookstore.service_library.dtos.NotificationDTO;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldSendNotification() throws Exception {

        doNothing().when(notificationService)
                .sendEmail(any(), any(), any());

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@gmail.com",
                        "subject": "Hello",
                        "message": "Test message"
                    }
                """))
                .andExpect(status().isOk());
    }
}