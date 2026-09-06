package ru.aston.user.service.exceptions;

public class EmailCheckException extends RuntimeException {
    public EmailCheckException(String message) {
        super(message);
    }
}
