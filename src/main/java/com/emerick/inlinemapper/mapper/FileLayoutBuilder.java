package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.FileSegment;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Builds {@link FileLayout} instances from declarative annotations.
 */
public final class FileLayoutBuilder {
    private FileLayoutBuilder() {
    }

    /**
     * Creates a {@link FileLayout} from a class annotated with {@link com.emerick.inlinemapper.annotation.FileLayout}.
     *
     * @param definitionClass class carrying {@link com.emerick.inlinemapper.annotation.FileLayout} annotation
     * @return built layout
     */
    public static FileLayout fromAnnotations(Class<?> definitionClass) {
        if (definitionClass == null) {
            throw new IllegalArgumentException("Definition class is required");
        }
        com.emerick.inlinemapper.annotation.FileLayout layoutAnnotation =
                definitionClass.getAnnotation(com.emerick.inlinemapper.annotation.FileLayout.class);
        if (layoutAnnotation == null) {
            throw new IllegalArgumentException("Class " + definitionClass.getName() + " is not annotated with @FileLayout");
        }

        FileLayout.Builder builder = FileLayout.builder();

        for (Field field : definitionClass.getDeclaredFields()) {
            FileSegment segment = field.getAnnotation(FileSegment.class);
            if (segment == null) {
                continue;
            }

            Class<?> type = resolveElementType(field);
            String id = segment.id().isEmpty() ? field.getName() : segment.id();
            boolean isList = segment.list() || java.util.List.class.isAssignableFrom(field.getType());

            if (segment.wildcard()) {
                builder.wildcard(id, type);
                continue;
            }

            int start = segment.position() != 0 ? segment.position() : segment.start();
            if (start == 0) {
                throw new IllegalArgumentException("Segment '" + id + "' must declare a position or start");
            }
            int end = segment.end() == 0 ? start : segment.end();

            boolean range = isList || start != end;
            if (range) {
                builder.range(id, start, end, type);
            } else {
                builder.line(id, start, type);
            }
        }

        return builder.build();
    }

    private static Class<?> resolveElementType(Field field) {
        if (java.util.List.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) genericType).getActualTypeArguments();
                if (args.length == 1 && args[0] instanceof Class<?>) {
                    return (Class<?>) args[0];
                }
            }
        }
        return field.getType();
    }
}
