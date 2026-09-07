package ru.aston.user.service.exception;

public class UserNotSaveException extends RuntimeException {
    public UserNotSaveException(String message) {
        super(message);
    }
}
