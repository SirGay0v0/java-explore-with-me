package ru.practicum.config;

import org.modelmapper.AbstractConverter;

import java.time.LocalDateTime;
import static ru.practicum.config.Constants.formatter;

public class LocalDateTimeToStringConverter extends AbstractConverter<LocalDateTime, String> {
    @Override
    protected String convert(LocalDateTime source) {
        return source != null ? source.format(formatter) : null;
    }
}