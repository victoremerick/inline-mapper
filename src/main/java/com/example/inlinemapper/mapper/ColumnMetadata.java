package com.example.inlinemapper.mapper;

import com.example.inlinemapper.annotation.Column;
import com.example.inlinemapper.annotation.LineEntity;
import com.example.inlinemapper.converter.TypeConverter;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Metadata about a field's positional column information.
 */
public class ColumnMetadata {
    private final Field field;
    private final Column column;
    private final TypeConverter<?> converter;

    public ColumnMetadata(Field field, Column column, TypeConverter<?> converter) {
        this.field = field;
        this.column = column;
        this.converter = converter;
    }

    public Field getField() {
        return field;
    }

    public Column getColumn() {
        return column;
    }

    public TypeConverter<?> getConverter() {
        return converter;
    }

    public int getPosition() {
        return column.position();
    }

    public int getLength() {
        return column.length();
    }

    public int getEndPosition() {
        return column.position() + column.length();
    }

    public String getDefaultValue() {
        return column.defaultValue();
    }

    public boolean shouldTrim() {
        return column.trim();
    }
}
