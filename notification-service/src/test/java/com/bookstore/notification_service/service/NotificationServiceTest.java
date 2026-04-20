package com.bookstore.notification_service.service;

import com.bookstore.notification_service.models.Notification;
import com.bookstore.notification_service.repositories.NotificationRepository;
import com.bookstore.notification_service.services.NotificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.mail.SimpleMailMessage;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    // ✅ SUCCESS
    @Test
    void shouldSendEmailSuccessfully() {

        notificationService.sendEmail("test@gmail.com", "Hello", "Message");

        // proverava da je mail poslat
        verify(mailSender).send(any(SimpleMailMessage.class));

        // proverava da je sačuvan sa statusom SENT
        verify(notificationRepository).save(argThat(n ->
                "SENT".equals(n.getStatus()) &&
                "test@gmail.com".equals(n.getEmail())
        ));
    }

    // ❌ FAILED
    @Test
    void shouldSetStatusFailed_whenMailFails() {

        doThrow(new RuntimeException())
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        notificationService.sendEmail("test@gmail.com", "Hello", "Message");

        verify(notificationRepository).save(argThat(n ->
                "FAILED".equals(n.getStatus())
        ));
    }
}