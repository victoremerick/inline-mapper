package com.emerick.inlinemapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class that declares file layout segments using {@link FileSegment} on its fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileLayout {
    /**
     * Optional inline segment list for declarative usage. Prefer field annotations.
     *
     * @return ordered segments
     */
    FileSegment[] value() default {};
}
