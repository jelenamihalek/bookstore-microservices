package com.bookstore.Util.exceptions;


public class InsufficientStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int requested;
    private int available;

    public InsufficientStockException(String message, int requested, int available) {
        super(message);
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}