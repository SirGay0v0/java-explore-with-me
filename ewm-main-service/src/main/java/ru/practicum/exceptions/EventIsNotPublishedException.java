package ru.practicum.exceptions;

public class EventIsNotPublishedException extends Exception {
    public EventIsNotPublishedException(String message) {
        super(message);
    }
}
