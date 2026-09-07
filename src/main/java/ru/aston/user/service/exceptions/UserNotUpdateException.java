package ru.aston.user.service.exceptions;

public class UserNotUpdateException extends RuntimeException{
    public UserNotUpdateException(Long id) {
        super("Пользователь с ID " + id + " не обновлен");
    }
}
