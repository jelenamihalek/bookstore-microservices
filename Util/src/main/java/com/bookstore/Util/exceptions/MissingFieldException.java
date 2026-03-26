package com.bookstore.Util.exceptions;

public class MissingFieldException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String field;

    public MissingFieldException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}