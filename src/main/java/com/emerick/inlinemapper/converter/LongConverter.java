package com.emerick.inlinemapper.converter;

/**
 * Converts between {@link String} and {@link Long}.
 */
public class LongConverter extends AbstractTypeConverter<Long> {
    @Override
    public Long fromString(String value) throws NumberFormatException {
        String trimmed = safeTrim(value);
        return trimmed != null ? Long.parseLong(trimmed) : null;
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
