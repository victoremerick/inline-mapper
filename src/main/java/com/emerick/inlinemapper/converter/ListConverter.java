package com.emerick.inlinemapper.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Converter for comma-separated lists.
 * Converts {@code List<String>} to/from delimited strings.
 */
public class ListConverter extends AbstractTypeConverter<List<String>> {
    private final String delimiter;

    /**
     * Creates a converter using comma as delimiter.
     */
    public ListConverter() {
        this(",");
    }

    /**
     * Creates a converter using a custom delimiter.
     *
     * @param delimiter delimiter used to join and split items
     */
    public ListConverter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public List<String> fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        if (trimmed == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(trimmed.split("\\" + delimiter)));
    }

    @Override
    public String toString(List<String> value) throws Exception {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return String.join(delimiter, value);
    }

    @Override
    public Class<List<String>> getType() {
        @SuppressWarnings("unchecked")
        Class<List<String>> listClass = (Class<List<String>>) (Class<?>) List.class;
        return listClass;
    }
}
