package ru.aston.user.service.exceptions;

public class UserNotDeleteException extends RuntimeException {
    public UserNotDeleteException(Long id) {
        super("Пользователь с ID " + id + " не удален");
    }
}
