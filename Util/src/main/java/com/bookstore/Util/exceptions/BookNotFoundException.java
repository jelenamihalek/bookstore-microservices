package com.bookstore.Util.exceptions;

public class BookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    public BookNotFoundException() {}

    public BookNotFoundException(String message, Integer userId) {
        super(message);
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}