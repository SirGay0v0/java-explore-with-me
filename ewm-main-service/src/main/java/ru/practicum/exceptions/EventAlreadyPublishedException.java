package ru.practicum.exceptions;

public class EventAlreadyPublishedException extends Exception {
    public EventAlreadyPublishedException(String message) {
        super(message);
    }
}
