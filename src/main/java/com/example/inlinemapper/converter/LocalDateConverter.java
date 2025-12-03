package com.example.inlinemapper.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Converter for LocalDate values.
 * Default format: yyyy-MM-dd
 */
public class LocalDateConverter extends AbstractTypeConverter<LocalDate> {
    private final DateTimeFormatter formatter;

    public LocalDateConverter() {
        this(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalDateConverter(String pattern) {
        this(DateTimeFormatter.ofPattern(pattern));
    }

    public LocalDateConverter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public LocalDate fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        return trimmed != null ? LocalDate.parse(trimmed, formatter) : null;
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }
}
