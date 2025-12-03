package com.example.inlinemapper.mapper;

import java.util.List;

/**
 * Interface for mapping between positional lines and objects.
 *
 * @param <T> the object type being mapped
 */
public interface LineMapper<T> {
    /**
     * Parses a line of text into an object of type T.
     *
     * @param line the line to parse
     * @return the parsed object
     * @throws MapperException if parsing fails
     */
    T toObject(String line);

    /**
     * Converts an object of type T to a positional line of text.
     *
     * @param object the object to convert
     * @return the line representation
     * @throws MapperException if conversion fails
     */
    String toLine(T object);

    /**
     * Parses multiple lines into a list of objects.
     *
     * @param lines the lines to parse
     * @return list of parsed objects
     * @throws MapperException if parsing fails
     */
    List<T> toObjects(List<String> lines);

    /**
     * Converts multiple objects to lines of text.
     *
     * @param objects the objects to convert
     * @return list of line representations
     * @throws MapperException if conversion fails
     */
    List<String> toLines(List<T> objects);
}
