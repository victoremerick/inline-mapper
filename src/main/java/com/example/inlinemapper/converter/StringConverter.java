package com.example.inlinemapper.converter;

public class StringConverter extends AbstractTypeConverter<String> {
    @Override
    public String fromString(String value) {
        return value;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
