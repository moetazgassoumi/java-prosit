package com.esprit.exceptions;

public class UniqueConstraintException extends ValidationException {
    public UniqueConstraintException(String message) {
        super(message);
    }
}