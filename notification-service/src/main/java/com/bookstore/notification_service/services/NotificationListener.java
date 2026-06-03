package com.bookstore.notification_service.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.notification_service.config.RabbitMQConfig;
import com.bookstore.service_library.events.OrderNotificationEvent;

@Service
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(
            queues = RabbitMQConfig.QUEUE
    )
    public void receive(OrderNotificationEvent event) {

        notificationService.sendEmail(
                event.getEmail(),
                event.getSubject(),
                event.getMessage()
        );
    }
}
