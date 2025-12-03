package com.emerick.inlinemapper.converter;

import java.util.UUID;

/**
 * Converter for UUID values.
 */
public class UUIDConverter extends AbstractTypeConverter<UUID> {
    @Override
    public UUID fromString(String value) throws IllegalArgumentException {
        String trimmed = safeTrim(value);
        return trimmed != null ? UUID.fromString(trimmed) : null;
    }

    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }
}
