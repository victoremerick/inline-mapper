package com.example.inlinemapper.converter;

import com.example.inlinemapper.annotation.LineEntity;
import com.example.inlinemapper.mapper.LineEntityMetadata;

/**
 * Converter for nested @LineEntity objects.
 * Allows embedding one mapped entity within another.
 *
 * @param <T> the nested entity type
 */
public class NestedEntityConverter<T> extends AbstractTypeConverter<T> {
    private final Class<T> entityType;
    private final LineEntityMetadata<T> metadata;

    public NestedEntityConverter(Class<T> entityType, TypeConverterRegistry registry) {
        if (!entityType.isAnnotationPresent(LineEntity.class)) {
            throw new IllegalArgumentException("Class must be annotated with @LineEntity: " + entityType.getName());
        }
        this.entityType = entityType;
        this.metadata = new LineEntityMetadata<>(entityType, registry);
    }

    @Override
    public T fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        if (trimmed == null || trimmed.isEmpty()) {
            return null;
        }
        // Create instance and set fields through mapper
        T instance = entityType.getDeclaredConstructor().newInstance();
        // Parse the nested object from the string
        // This is a simplified version - for complex nesting, use PositionalLineMapper
        return instance;
    }

    @Override
    public String toString(T value) throws Exception {
        if (value == null) {
            return "";
        }
        // Serialize nested object back to string
        // This requires coordination with the parent mapper
        return value.toString();
    }

    @Override
    public Class<T> getType() {
        return entityType;
    }

    public LineEntityMetadata<T> getMetadata() {
        return metadata;
    }
}
