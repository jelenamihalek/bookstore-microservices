package com.bookstore.Util.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    public UserNotFoundException() {}

    public UserNotFoundException(String message, Integer userId) {
        super(message);
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}