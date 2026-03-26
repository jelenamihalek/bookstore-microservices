package com.bookstore.Util.exceptions;


public class UserNotFoundByEmailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String email;

    public UserNotFoundByEmailException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}