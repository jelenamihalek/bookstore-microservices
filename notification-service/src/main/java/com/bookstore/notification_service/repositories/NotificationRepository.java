package com.bookstore.notification_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.notification_service.models.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}