package com.bookstore.Util.exceptions;

public class OrderAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    public OrderAccessDeniedException(String message, Integer orderId) {
        super(message);
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }
}