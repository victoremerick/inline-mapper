package com.emerick.inlinemapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a positional column in a line.
 * The field value will be extracted from the specified position and length.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * The starting position (0-indexed) of the column in the line.
     *
     * @return the starting position
     */
    int position();

    /**
     * The length of the column.
     *
     * @return the column length
     */
    int length();

    /**
     * Optional default value if the column is empty or missing.
     *
     * @return the default value
     */
    String defaultValue() default "";

    /**
     * Whether to trim whitespace from the extracted value.
     *
     * @return true to trim, false otherwise
     */
    boolean trim() default true;
}
