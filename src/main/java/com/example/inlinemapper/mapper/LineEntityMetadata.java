package com.example.inlinemapper.mapper;

import com.example.inlinemapper.annotation.Column;
import com.example.inlinemapper.annotation.LineEntity;
import com.example.inlinemapper.converter.TypeConverter;
import com.example.inlinemapper.converter.TypeConverterRegistry;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Metadata about a class that is annotated with {@link LineEntity}.
 */
public class LineEntityMetadata<T> {
    private final Class<T> entityClass;
    private final List<ColumnMetadata> columns;
    private final String separator;

    public LineEntityMetadata(Class<T> entityClass, TypeConverterRegistry converterRegistry) {
        if (!entityClass.isAnnotationPresent(LineEntity.class)) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not annotated with @LineEntity");
        }

        this.entityClass = entityClass;
        LineEntity lineEntity = entityClass.getAnnotation(LineEntity.class);
        this.separator = lineEntity.separator();
        this.columns = extractColumns(converterRegistry);
    }

    private List<ColumnMetadata> extractColumns(TypeConverterRegistry converterRegistry) {
        List<ColumnMetadata> result = new ArrayList<>();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                TypeConverter<?> converter = converterRegistry.getConverter(field.getType());
                field.setAccessible(true);

                result.add(new ColumnMetadata(field, column, converter));
            }
        }

        // Sort by position for consistent ordering
        result.sort(Comparator.comparingInt(ColumnMetadata::getPosition));
        return result;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public List<ColumnMetadata> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public String getSeparator() {
        return separator;
    }

    public int getTotalLength() {
        if (columns.isEmpty()) {
            return 0;
        }
        ColumnMetadata last = columns.get(columns.size() - 1);
        return last.getEndPosition();
    }
}
