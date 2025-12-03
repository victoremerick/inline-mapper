package com.example.inlinemapper.converter;

import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for advanced type converters.
 */
public class AdvancedConvertersTest {
    private TypeConverterRegistry registry;

    @Before
    public void setUp() {
        registry = new TypeConverterRegistry();
    }

    @Test
    public void testBigDecimalConverter() throws Exception {
        TypeConverter<BigDecimal> converter = registry.getConverter(BigDecimal.class);
        
        BigDecimal value = converter.fromString("123.45");
        assertEquals(new BigDecimal("123.45"), value);
        
        String str = converter.toString(value);
        assertEquals("123.45", str);
    }

    @Test
    public void testBigDecimalConverterWithNullAndEmpty() throws Exception {
        TypeConverter<BigDecimal> converter = registry.getConverter(BigDecimal.class);
        
        assertNull(converter.fromString(null));
        assertNull(converter.fromString(""));
        assertEquals("", converter.toString(null));
    }

    @Test
    public void testLocalDateConverter() throws Exception {
        TypeConverter<LocalDate> converter = registry.getConverter(LocalDate.class);
        
        LocalDate value = converter.fromString("2025-12-03");
        assertEquals(LocalDate.of(2025, 12, 3), value);
        
        String str = converter.toString(value);
        assertEquals("2025-12-03", str);
    }

    @Test
    public void testLocalDateConverterWithCustomFormat() throws Exception {
        TypeConverter<LocalDate> converter = new LocalDateConverter("yyyyMMdd");
        
        LocalDate value = converter.fromString("20251203");
        assertEquals(LocalDate.of(2025, 12, 3), value);
        
        String str = converter.toString(value);
        assertEquals("20251203", str);
    }

    @Test
    public void testUUIDConverter() throws Exception {
        TypeConverter<UUID> converter = registry.getConverter(UUID.class);
        
        String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
        UUID value = converter.fromString(uuidStr);
        assertEquals(UUID.fromString(uuidStr), value);
        
        String str = converter.toString(value);
        assertEquals(uuidStr, str);
    }

    @Test
    public void testEnumConverter() throws Exception {
        TypeConverter<Status> converter = new EnumConverter<>(Status.class);
        
        Status value = converter.fromString("ACTIVE");
        assertEquals(Status.ACTIVE, value);
        
        String str = converter.toString(value);
        assertEquals("ACTIVE", str);
    }

    @Test
    public void testListConverter() throws Exception {
        TypeConverter<java.util.List<String>> converter = new ListConverter(",");
        
        java.util.List<String> value = converter.fromString("one,two,three");
        assertEquals(3, value.size());
        assertEquals("one", value.get(0));
        assertEquals("two", value.get(1));
        assertEquals("three", value.get(2));
        
        String str = converter.toString(value);
        assertEquals("one,two,three", str);
    }

    @Test
    public void testListConverterWithCustomDelimiter() throws Exception {
        TypeConverter<java.util.List<String>> converter = new ListConverter("|");
        
        java.util.List<String> value = converter.fromString("a|b|c");
        assertEquals(3, value.size());
    }

    @Test
    public void testRegistryHasAllDefaultConverters() {
        assertTrue(registry.hasConverter(String.class));
        assertTrue(registry.hasConverter(Integer.class));
        assertTrue(registry.hasConverter(Long.class));
        assertTrue(registry.hasConverter(Double.class));
        assertTrue(registry.hasConverter(Boolean.class));
        assertTrue(registry.hasConverter(BigDecimal.class));
        assertTrue(registry.hasConverter(LocalDate.class));
        assertTrue(registry.hasConverter(UUID.class));
    }

    @Test
    public void testRegistryCustomConverter() throws Exception {
        TypeConverter<Status> customConverter = new EnumConverter<>(Status.class);
        registry.register(Status.class, customConverter);
        
        TypeConverter<Status> retrieved = registry.getConverter(Status.class);
        Status value = retrieved.fromString("INACTIVE");
        assertEquals(Status.INACTIVE, value);
    }

    /**
     * Test enum for converter testing.
     */
    public enum Status {
        ACTIVE, INACTIVE, PENDING
    }
}
