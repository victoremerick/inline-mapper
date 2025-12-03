package com.example.inlinemapper.converter;

public class IntegerConverter extends AbstractTypeConverter<Integer> {
    @Override
    public Integer fromString(String value) throws NumberFormatException {
        String trimmed = safeTrim(value);
        return trimmed != null ? Integer.parseInt(trimmed) : null;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
