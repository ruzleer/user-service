package ru.aston.user.service.exception;

public class UserNotDeleteException extends RuntimeException {
    public UserNotDeleteException(Long id) {
        super("Пользователь с ID " + id + " не удален");
    }
}
