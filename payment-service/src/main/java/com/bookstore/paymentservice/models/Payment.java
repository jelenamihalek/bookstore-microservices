package com.bookstore.paymentservice.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="payments")
public class Payment {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    private int orderId;
	    private int userId;
	    private double amount;
	    private String status;
	    private LocalDateTime paymentDate;
	    private String transactionReference;

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getOrderId() {
			return orderId;
		}
		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public int getUserId() {
			return userId;
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
	
		public LocalDateTime getPaymentDate() {
			return paymentDate;
		}
		public void setPaymentDate(LocalDateTime paymentDate) {
			this.paymentDate = paymentDate;
		}
		public String getTransactionReference() {
			return transactionReference;
		}
		public void setTransactionReference(String transactionReference) {
			this.transactionReference = transactionReference;
		}
		
		
}
