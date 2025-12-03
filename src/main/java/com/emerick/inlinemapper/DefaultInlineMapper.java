package com.emerick.inlinemapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Default implementation of InlineMapper using function-based mapping.
 */
public class DefaultInlineMapper implements InlineMapper {
    private final Map<String, Function<?, ?>> mappers = new HashMap<>();

    /**
     * Registers a mapper function for a given type pair.
     *
     * @param key    unique key for the mapper
     * @param mapper the mapping function
     * @param <T>    the type of the source object
     * @param <U>    the type of the target object
     */
    public <T, U> void register(String key, Function<T, U> mapper) {
        mappers.put(key, mapper);
    }

    @Override
    public <T, U> U map(T source) {
        String key = source.getClass().getSimpleName();
        @SuppressWarnings("unchecked")
        Function<T, U> mapper = (Function<T, U>) mappers.get(key);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper registered for type: " + key);
        }
        return mapper.apply(source);
    }
}
