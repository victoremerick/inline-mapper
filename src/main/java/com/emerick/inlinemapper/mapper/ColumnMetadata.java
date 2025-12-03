package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.Column;
import com.emerick.inlinemapper.converter.TypeConverter;

import java.lang.reflect.Field;

/**
 * Metadata about a field's positional column information.
 */
public class ColumnMetadata {
    private final Field field;
    private final Column column;
    private final TypeConverter<?> converter;

    /**
     * Creates metadata for a single annotated field.
     *
     * @param field     reflected field
     * @param column    column annotation
     * @param converter converter bound to the field type
     */
    public ColumnMetadata(Field field, Column column, TypeConverter<?> converter) {
        this.field = field;
        this.column = column;
        this.converter = converter;
    }

    /**
     * @return the reflected field reference
     */
    public Field getField() {
        return field;
    }

    /**
     * @return column annotation attributes
     */
    public Column getColumn() {
        return column;
    }

    /**
     * @return converter for the field type
     */
    public TypeConverter<?> getConverter() {
        return converter;
    }

    /**
     * @return starting position (0-indexed)
     */
    public int getPosition() {
        return column.position();
    }

    /**
     * @return declared column length
     */
    public int getLength() {
        return column.length();
    }

    /**
     * @return end position (exclusive)
     */
    public int getEndPosition() {
        return column.position() + column.length();
    }

    /**
     * @return default value if provided
     */
    public String getDefaultValue() {
        return column.defaultValue();
    }

    /**
     * @return true when whitespace should be trimmed
     */
    public boolean shouldTrim() {
        return column.trim();
    }
}
