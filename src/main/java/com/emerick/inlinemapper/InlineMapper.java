package com.emerick.inlinemapper;

import com.emerick.inlinemapper.mapper.LineMapper;

/**
 * Legacy generic mapper interface.
 * For new code, use {@link LineMapper} for positional line mapping.
 *
 * @deprecated Use {@link LineMapper} instead
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
