package com.bookstore.Util.exceptions;

public class BookAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String title;

    public BookAlreadyExistsException(String message, String title) {
        super(message);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}