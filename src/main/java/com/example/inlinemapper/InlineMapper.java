package com.example.inlinemapper;

/**
 * Legacy generic mapper interface.
 * For new code, use {@link com.example.inlinemapper.mapper.LineMapper} for positional line mapping.
 *
 * @deprecated Use {@link com.example.inlinemapper.mapper.LineMapper} instead
 */
@Deprecated
public interface InlineMapper {
    /**
     * Maps an object of type T to type U.
     *
     * @param source the source object to map
     * @param <T>    the type of the source object
     * @param <U>    the type of the target object
     * @return the mapped object
     */
    <T, U> U map(T source);
}
