package ru.aston.user.service.exception;

public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String operation, Long id, Throwable cause) {
        super("Не удалось выполнить операцию '%s' для пользователя с ID %d"
                .formatted(operation, id), cause);
    }

    public DatabaseOperationException(String message) {
        super(message);
    }
}
