package com.example.inlinemapper.converter;

/**
 * Enum-based converter for types that can be represented as enums.
 * Handles conversion from/to enum values.
 *
 * @param <T> the enum type
 */
public class EnumConverter<T extends Enum<T>> extends AbstractTypeConverter<T> {
    private final Class<T> enumType;

    public EnumConverter(Class<T> enumType) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("Class must be an Enum: " + enumType.getName());
        }
        this.enumType = enumType;
    }

    @Override
    public T fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        if (trimmed == null) {
            return null;
        }
        return Enum.valueOf(enumType, trimmed.toUpperCase());
    }

    @Override
    public String toString(T value) throws Exception {
        return value != null ? value.name() : "";
    }

    @Override
    public Class<T> getType() {
        return enumType;
    }
}
