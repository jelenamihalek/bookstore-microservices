package com.bookstore.Util.exceptions;

public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    public OrderNotFoundException(String message, Integer orderId) {
        super(message);
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }
}
