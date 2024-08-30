package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final InvalidDateTimeException e) {
        List<StackTraceElement> list = List.of(e.getStackTrace());
        log.info("event is already canceled");
        ApiError apiError = new ApiError();
        apiError.setErrors(list);
        if (e.getCause() != null) {
            apiError.setReason(e.getCause().toString());
        }
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.BAD_REQUEST.toString());
        return apiError;
    }
}
