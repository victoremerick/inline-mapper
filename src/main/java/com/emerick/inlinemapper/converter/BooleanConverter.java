package com.emerick.inlinemapper.converter;

/**
 * Converts between {@link String} values and {@link Boolean} flags.
 */
public class BooleanConverter extends AbstractTypeConverter<Boolean> {
    @Override
    public Boolean fromString(String value) {
        String trimmed = safeTrim(value);
        if (trimmed == null) {
            return null;
        }
        return trimmed.equalsIgnoreCase("true") || trimmed.equals("1");
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
