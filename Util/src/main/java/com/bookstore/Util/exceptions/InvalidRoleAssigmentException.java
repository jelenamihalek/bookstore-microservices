package com.bookstore.Util.exceptions;

public class InvalidRoleAssigmentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String attemptedRole;

    public InvalidRoleAssigmentException() {
    }

    public InvalidRoleAssigmentException(String message, String attemptedRole) {
        super(message);
        this.attemptedRole = attemptedRole;
    }

    public String getAttemptedRole() {
        return attemptedRole;
    }

    public void setAttemptedRole(String attemptedRole) {
        this.attemptedRole = attemptedRole;
    }
}