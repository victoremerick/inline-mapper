package com.example.inlinemapper.converter;

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
