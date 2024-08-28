package ru.practicum.exceptions;

public class NotFoundException extends Exception {
    String message;

    public NotFoundException(String message) {
        super(message);
    }
}

