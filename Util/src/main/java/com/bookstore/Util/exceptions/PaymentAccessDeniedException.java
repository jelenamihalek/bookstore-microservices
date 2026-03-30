package com.bookstore.Util.exceptions;

public class PaymentAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Integer paymentId;

    public PaymentAccessDeniedException(String message, Integer paymentId) {
        super(message);
        this.paymentId = paymentId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }
}