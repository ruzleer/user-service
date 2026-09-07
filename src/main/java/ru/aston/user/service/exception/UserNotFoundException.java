package ru.aston.user.service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
