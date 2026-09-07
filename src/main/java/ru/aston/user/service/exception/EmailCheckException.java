package ru.aston.user.service.exception;

public class EmailCheckException extends RuntimeException {
    public EmailCheckException(String message) {
        super(message);
    }
}
