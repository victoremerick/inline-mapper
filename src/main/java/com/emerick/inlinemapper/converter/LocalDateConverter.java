package com.emerick.inlinemapper.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

    /**
     * Converter for LocalDate values.
     * Default format: yyyy-MM-dd
     */
    public class LocalDateConverter extends AbstractTypeConverter<LocalDate> {
        private final DateTimeFormatter formatter;

        /**
         * Creates a converter using ISO_LOCAL_DATE.
         */
        public LocalDateConverter() {
            this(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        /**
         * Creates a converter using a custom date pattern.
         *
         * @param pattern date pattern accepted by {@link DateTimeFormatter#ofPattern(String)}
         */
        public LocalDateConverter(String pattern) {
            this(DateTimeFormatter.ofPattern(pattern));
        }

        /**
         * Creates a converter using a provided formatter.
         *
         * @param formatter date formatter to use
         */
        public LocalDateConverter(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

    @Override
    public LocalDate fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        return trimmed != null ? LocalDate.parse(trimmed, formatter) : null;
    }

    @Override
    public String toString(LocalDate value) throws Exception {
        if (value == null) {
            return "";
        }
        return value.format(formatter);
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }
}
