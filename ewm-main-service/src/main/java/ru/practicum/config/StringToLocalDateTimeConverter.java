package ru.practicum.config;

import org.modelmapper.AbstractConverter;

import java.time.LocalDateTime;

import static ru.practicum.config.Constants.formatter;

public class StringToLocalDateTimeConverter extends AbstractConverter<String, LocalDateTime> {
    @Override
    protected LocalDateTime convert(String source) {
        return LocalDateTime.parse(source, formatter);
    }
}
