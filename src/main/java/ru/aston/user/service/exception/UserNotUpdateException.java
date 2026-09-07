package ru.aston.user.service.exception;

public class UserNotUpdateException extends RuntimeException{
    public UserNotUpdateException(Long id) {
        super("Пользователь с ID " + id + " не обновлен");
    }
}
