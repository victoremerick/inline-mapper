package com.emerick.inlinemapper.converter;

/**
 * Converts between {@link String} and {@link Double}.
 */
public class DoubleConverter extends AbstractTypeConverter<Double> {
    @Override
    public Double fromString(String value) throws NumberFormatException {
        String trimmed = safeTrim(value);
        return trimmed != null ? Double.parseDouble(trimmed) : null;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
