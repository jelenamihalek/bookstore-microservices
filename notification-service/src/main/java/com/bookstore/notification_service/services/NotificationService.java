package com.bookstore.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bookstore.notification_service.models.Notification;
import com.bookstore.notification_service.repositories.NotificationRepository;

@Service
public class NotificationService {
	
	  @Autowired
	    private JavaMailSender mailSender;

	    @Autowired
	    private NotificationRepository notificationRepository;

	    public void sendEmail(String to, String subject, String text) {

	        Notification notification = new Notification();

	        notification.setEmail(to);
	        notification.setSubject(subject);
	        notification.setMessage(text);

	        try {
	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setTo(to);
	            message.setSubject(subject);
	            message.setText(text);

	            mailSender.send(message);

	            notification.setStatus("SENT");

	        } catch (Exception e) {
	            notification.setStatus("FAILED");
	        }

	        notificationRepository.save(notification);
	    }
}
