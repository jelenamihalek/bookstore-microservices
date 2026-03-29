package com.bookstore.Util.exceptions;


public class InvalidBookQuantityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int quantity;

    public InvalidBookQuantityException(String message, int quantity) {
        super(message);
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
