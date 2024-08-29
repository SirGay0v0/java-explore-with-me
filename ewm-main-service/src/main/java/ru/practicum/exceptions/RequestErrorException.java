package ru.practicum.exceptions;

public class RequestErrorException extends Exception {
    public RequestErrorException(String message) {
        super(message);
    }
}
