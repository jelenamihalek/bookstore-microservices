package com.bookstore.notification_service.controllers;


import org.springframework.web.bind.annotation.*;

import com.bookstore.notification_service.services.NotificationService;
import com.bookstore.service_library.dtos.NotificationDTO;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public void send(@RequestBody NotificationDTO dto) {

        notificationService.sendEmail(
                dto.getEmail(),
                dto.getSubject(),
                dto.getMessage()
        );
    }
}