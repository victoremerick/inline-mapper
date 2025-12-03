package com.emerick.inlinemapper;

import com.emerick.inlinemapper.mapper.PositionalLineMapperTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DefaultInlineMapper.
 * @deprecated Use {@link PositionalLineMapperTest} instead
 */
@Deprecated
public class DefaultInlineMapperTest {

    @Test
    public void testMapWithRegisteredMapper() {
        DefaultInlineMapper mapper = new DefaultInlineMapper();
        mapper.register("String", s -> ((String) s).toUpperCase());

        String result = mapper.map("hello");
        assertEquals("HELLO", result);
    }

    @Test
    public void testMapWithIntegerToString() {
        DefaultInlineMapper mapper = new DefaultInlineMapper();
        mapper.register("Integer", i -> "Number: " + i);

        String result = mapper.map(42);
        assertEquals("Number: 42", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapWithUnregisteredType() {
        DefaultInlineMapper mapper = new DefaultInlineMapper();
        mapper.map("test");
    }
}
