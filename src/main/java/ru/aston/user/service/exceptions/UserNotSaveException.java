package ru.aston.user.service.exceptions;

public class UserNotSaveException extends RuntimeException {
    public UserNotSaveException(String message) {
        super(message);
    }
}
