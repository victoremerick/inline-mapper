package com.emerick.inlinemapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a segment of a file mapped to the annotated field's type.
 * Supports single lines, fixed ranges, and wildcard list sections.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FileSegment {
    /**
     * Identifier used to retrieve the mapped section (defaults to field name).
     *
     * @return segment id
     */
    String id() default "";

    /**
     * 1-based line position. Negative values address lines from the end (-1 is last line).
     * Acts as an alias for start when end is not provided.
     *
     * @return single position value
     */
    int position() default 0;

    /**
     * 1-based starting line. Negative values address lines from the end (-1 is last line).
     *
     * @return start line
     */
    int start() default 0;

    /**
     * 1-based ending line. Negative values address lines from the end (-1 is last line).
     * If omitted (0) the start/position value is used.
     *
     * @return end line
     */
    int end() default 0;

    /**
     * Flag indicating this segment is a list/range.
     *
     * @return true when list/range
     */
    boolean list() default false;

    /**
     * Flag indicating this segment is a wildcard list (size resolved dynamically).
     *
     * @return true when wildcard list
     */
    boolean wildcard() default false;
}
