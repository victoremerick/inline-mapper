package com.example.inlinemapper.mapper;

import com.example.inlinemapper.converter.TypeConverter;
import com.example.inlinemapper.converter.TypeConverterRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LineMapper for positional content mapping.
 * Maps between positional fixed-width lines and objects.
 *
 * @param <T> the object type being mapped
 */
public class PositionalLineMapper<T> implements LineMapper<T> {
    private final LineEntityMetadata<T> metadata;
    private final TypeConverterRegistry converterRegistry;

    public PositionalLineMapper(Class<T> entityClass) {
        this(entityClass, new TypeConverterRegistry());
    }

    public PositionalLineMapper(Class<T> entityClass, TypeConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
        this.metadata = new LineEntityMetadata<>(entityClass, converterRegistry);
    }

    @Override
    public T toObject(String line) {
        try {
            T instance = metadata.getEntityClass().getDeclaredConstructor().newInstance();

            for (ColumnMetadata column : metadata.getColumns()) {
                String value = extractValue(line, column);
                setFieldValue(instance, column, value);
            }

            return instance;
        } catch (Exception e) {
            throw new MapperException("Failed to parse line: " + line, e);
        }
    }

    @Override
    public String toLine(T object) {
        try {
            StringBuilder line = new StringBuilder();
            int currentPosition = 0;

            for (ColumnMetadata column : metadata.getColumns()) {
                // Pad with spaces if there's a gap
                while (currentPosition < column.getPosition()) {
                    line.append(' ');
                    currentPosition++;
                }

                // Get the field value and convert to string
                Object fieldValue = column.getField().get(object);
                String stringValue = convertToString(column, fieldValue);

                // Pad or truncate to the exact length
                stringValue = padValue(stringValue, column.getLength());
                line.append(stringValue);
                currentPosition += column.getLength();
            }

            return line.toString();
        } catch (Exception e) {
            throw new MapperException("Failed to convert object to line", e);
        }
    }

    @Override
    public List<T> toObjects(List<String> lines) {
        List<T> objects = new ArrayList<>();
        for (String line : lines) {
            objects.add(toObject(line));
        }
        return objects;
    }

    @Override
    public List<String> toLines(List<T> objects) {
        List<String> lines = new ArrayList<>();
        for (T object : objects) {
            lines.add(toLine(object));
        }
        return lines;
    }

    private String extractValue(String line, ColumnMetadata column) {
        int endPosition = Math.min(column.getEndPosition(), line.length());
        int startPosition = column.getPosition();

        if (startPosition >= line.length()) {
            return column.getDefaultValue();
        }

        String value = line.substring(startPosition, endPosition);

        if (column.shouldTrim()) {
            value = value.trim();
        }

        return value.isEmpty() ? column.getDefaultValue() : value;
    }

    private void setFieldValue(T instance, ColumnMetadata column, String value) throws Exception {
        Field field = column.getField();
        TypeConverter<?> converter = column.getConverter();

        if (value.isEmpty() && !column.getDefaultValue().isEmpty()) {
            value = column.getDefaultValue();
        }

        if (value.isEmpty() && field.getType().isPrimitive()) {
            // Skip setting for empty values with primitive types
            return;
        }

        Object convertedValue = convertFromString(converter, value);
        field.set(instance, convertedValue);
    }

    private String convertToString(ColumnMetadata column, Object value) throws Exception {
        if (value == null) {
            return "";
        }

        @SuppressWarnings("unchecked")
        TypeConverter<Object> converter = (TypeConverter<Object>) column.getConverter();
        return converter.toString(value);
    }

    @SuppressWarnings("unchecked")
    private Object convertFromString(TypeConverter<?> converter, String value) throws Exception {
        if (value.isEmpty()) {
            return null;
        }
        return ((TypeConverter<Object>) converter).fromString(value);
    }

    private String padValue(String value, int length) {
        if (value.length() == length) {
            return value;
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
    }
}
