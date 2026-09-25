package ru.aston.user.service.kafka;

public record UserEvent(UserOperation operation, String email) {}
