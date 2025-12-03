package com.emerick.inlinemapper.converter;

/**
 * Abstract base class for TypeConverter implementations.
 * Provides default implementations for helper methods.
 *
 * @param <T> the type being converted
 */
public abstract class AbstractTypeConverter<T> implements TypeConverter<T> {
    @Override
    public String toString(T value) throws Exception {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public boolean canHandle(Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.equals(getType()) || 
               getType().isAssignableFrom(type) || 
               type.isAssignableFrom(getType());
    }

    @Override
    public String getName() {
        return getType().getSimpleName() + "Converter";
    }

    /**
     * Helper method to safely trim string values.
     *
     * @param value the value to trim
     * @return trimmed value, or null if empty
     */
    protected String safeTrim(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
