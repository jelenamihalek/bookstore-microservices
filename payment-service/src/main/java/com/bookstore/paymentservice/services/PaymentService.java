package com.bookstore.paymentservice.services;

import com.bookstore.Util.exceptions.PaymentAccessDeniedException;
import com.bookstore.Util.exceptions.PaymentNotFoundException;
import com.bookstore.Util.exceptions.UserNotFoundByEmailException;
import com.bookstore.Util.exceptions.UserNotFoundException;
import com.bookstore.paymentservice.models.Payment;
import com.bookstore.paymentservice.repositories.PaymentRepository;
import com.bookstore.service_library.clients.UserClient;
import com.bookstore.service_library.decoder.Decoder;
import com.bookstore.service_library.dtos.UserDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

	 
	    private final PaymentRepository paymentRepository;
	    
	    @Autowired
	    private Decoder decoder;

	    @Autowired
	    private UserClient userClient;
	 
		public PaymentService(PaymentRepository paymentRepository,Decoder decoder,UserClient userClient) {
			this.paymentRepository=paymentRepository;
			this.decoder=decoder;
			this.userClient=userClient;
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
	    
	 
	    public List<Payment> getAllPayments() {
	        return paymentRepository.findAll();
	    }

	    public List<Payment> getPaymentsForUser(String authorization) {

	        String email = decoder.decodeHeader(authorization);

	        UserDTO user = userClient.findByEmail(email);

	        if (user == null) {
	            throw new UserNotFoundByEmailException("User not found", email);
	        }

	        return paymentRepository.findByUserId(user.getId());
	    }

	    public Payment getPaymentById(int id, String authorization) {

	        String email = decoder.decodeHeader(authorization);

	        UserDTO user = userClient.findByEmail(email);

	        if (user == null) {
	            throw new UserNotFoundException("User not found", null);
	        }

	        Payment payment = paymentRepository.findById(id)
	                .orElseThrow(() -> new PaymentNotFoundException("Payment not found", id));

	        if (payment.getUserId() != user.getId()) {
	            throw new PaymentAccessDeniedException(
	                    "You cannot access this payment",
	                    id
	            );
	        }
	        return payment;
	    }

	    public Payment updatePayment(int id, Payment updated) {

	        Payment payment = paymentRepository.findById(id)
	                .orElseThrow(() -> new PaymentNotFoundException("Payment not found", id));

	        payment.setStatus(updated.getStatus());

	        return paymentRepository.save(payment);
	    }

	    public void deletePayment(int id) {

	        Payment payment = paymentRepository.findById(id)
	                .orElseThrow(() -> new PaymentNotFoundException("Payment not found", id));

	        paymentRepository.delete(payment);
	    }
}
