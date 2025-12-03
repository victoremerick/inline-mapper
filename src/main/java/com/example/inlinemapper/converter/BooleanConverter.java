package com.example.inlinemapper.converter;

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
