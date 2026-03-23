package com.bookstore.paymentservice.services;

import com.bookstore.paymentservice.models.Payment;
import com.bookstore.paymentservice.repositories.PaymentRepository;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

	 
	    private final PaymentRepository paymentRepository;
	 
		public PaymentService(PaymentRepository paymentRepository) {
			this.paymentRepository=paymentRepository;
		}

	    public Payment processPayment(Payment payment) {

	        // simulacija (80% success)
	        if (Math.random() > 0.2) {
	            payment.setStatus("SUCCESS");
	        } else {
	            payment.setStatus("FAILED");
	        }

	        return paymentRepository.save(payment);
	    }
}
