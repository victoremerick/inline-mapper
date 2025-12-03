package com.emerick.inlinemapper.converter;

/**
 * Pass-through converter for {@link String} values.
 */
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
