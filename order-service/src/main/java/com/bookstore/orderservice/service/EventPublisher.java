package com.bookstore.orderservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.orderservice.config.RabbitMQConfig;
import com.bookstore.service_library.events.OrderNotificationEvent;

@Service
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(
            OrderNotificationEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.QUEUE,
                event);
    }
}