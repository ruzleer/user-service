package ru.aston.user.service.exception;

public class ValidationException extends RuntimeException {
    private final String field;
    private final String value;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public ValidationException(String field, String value, String message) {
        super("Ошибка валидации поля '" + field + "': " + message);
        this.field = field;
        this.value = value;
    }

    public String getField() { return field; }
    public String getValue() { return value; }
}
