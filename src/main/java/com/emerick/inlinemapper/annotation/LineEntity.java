package com.emerick.inlinemapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a positional line mapped entity.
 * Fields annotated with {@link Column} will be mapped to/from positional content.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LineEntity {
    /**
     * Optional line separator. Default is newline.
     *
     * @return the line separator character(s)
     */
    String separator() default "\n";
}
