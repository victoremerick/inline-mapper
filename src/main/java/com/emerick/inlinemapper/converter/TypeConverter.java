package com.emerick.inlinemapper.converter;

/**
 * Enhanced converter interface supporting both simple and complex type conversions.
 * Supports mapping between strings and any type T (primitives, objects, etc).
 *
 * @param <T> the type being converted to/from
 */
public interface TypeConverter<T> {
    /**
     * Converts a string value to the target type.
     *
     * @param value the string value to convert
     * @return the converted value of type T
     * @throws Exception if conversion fails
     */
    T fromString(String value) throws Exception;

    /**
     * Converts a value of type T to a string representation.
     *
     * @param value the value to convert (can be null)
     * @return the string representation
     * @throws Exception if conversion fails
     */
    String toString(T value) throws Exception;

    /**
     * Gets the type this converter handles.
     *
     * @return the target type class
     */
    Class<T> getType();

    /**
     * Optional: Check if this converter can handle the given type.
     * Used for flexible type matching.
     *
     * @param type the type to check
     * @return true if this converter can handle the type
     */
    default boolean canHandle(Class<?> type) {
        return type != null && (type.equals(getType()) || 
                getType().isAssignableFrom(type) || 
                type.isAssignableFrom(getType()));
    }

    /**
     * Optional: Get a human-readable name for this converter.
     *
     * @return converter name
     */
    default String getName() {
        return getType().getSimpleName() + "Converter";
    }
}
