package com.bookstore.Util.exceptions;


public class PaymentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Integer paymentId;

    public PaymentNotFoundException(String message, Integer paymentId) {
        super(message);
        this.paymentId = paymentId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }
}
