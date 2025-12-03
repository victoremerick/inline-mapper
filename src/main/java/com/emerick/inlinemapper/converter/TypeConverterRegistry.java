package com.emerick.inlinemapper.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for type converters.
 * Supports registering and retrieving converters with flexible type matching.
 */
public class TypeConverterRegistry {
    private final Map<Class<?>, TypeConverter<?>> converters = new HashMap<>();

    /**
     * Creates a registry preloaded with built-in converters.
     */
    public TypeConverterRegistry() {
        registerDefaultConverters();
    }

    private void registerDefaultConverters() {
        register(new StringConverter());
        register(new IntegerConverter());
        register(new LongConverter());
        register(new DoubleConverter());
        register(new BooleanConverter());
        register(new BigDecimalConverter());
        register(new LocalDateConverter());
        register(new UUIDConverter());
    }

    /**
     * Registers a type converter.
     *
     * @param converter the converter to register
     * @param <T>       the type being converted
     */
    public <T> void register(TypeConverter<T> converter) {
        converters.put(converter.getType(), converter);
    }

    /**
     * Registers a converter for a specific type (overrides automatic type detection).
     *
     * @param type      the type to register for
     * @param converter the converter
     * @param <T>       the type being converted
     */
    public <T> void register(Class<T> type, TypeConverter<T> converter) {
        converters.put(type, converter);
    }

    /**
     * Gets a converter for a specific type.
     * Searches for exact match first, then tries assignable types.
     *
     * @param type the type to resolve
     * @param <T>  target type
     * @return the converter, or StringConverter if not found
     */
    @SuppressWarnings("unchecked")
    public <T> TypeConverter<T> getConverter(Class<T> type) {
        // Exact match
        TypeConverter<?> converter = converters.get(type);
        if (converter != null) {
            return (TypeConverter<T>) converter;
        }

        // Try to find compatible converter
        for (Map.Entry<Class<?>, TypeConverter<?>> entry : converters.entrySet()) {
            if (entry.getValue().canHandle(type)) {
                return (TypeConverter<T>) entry.getValue();
            }
        }

        // Default to StringConverter for unknown types
        return (TypeConverter<T>) converters.get(String.class);
    }

    /**
     * Check if a converter is registered for a type.
     *
     * @param type the type
     * @return true if converter exists
     */
    public boolean hasConverter(Class<?> type) {
        return converters.containsKey(type) || 
               converters.values().stream().anyMatch(c -> c.canHandle(type));
    }

    /**
     * Get the number of registered converters.
     *
     * @return converter count
     */
    public int size() {
        return converters.size();
    }
}
