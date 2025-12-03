package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.Column;
import com.emerick.inlinemapper.annotation.LineEntity;
import com.emerick.inlinemapper.converter.EnumConverter;
import com.emerick.inlinemapper.converter.LocalDateConverter;
import com.emerick.inlinemapper.converter.TypeConverterRegistry;
import com.example.inlinemapper.converter.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Test enum for object type mapping.
 */
enum Color {
    RED, GREEN, BLUE, YELLOW
}

/**
 * Test entity with complex object types.
 */
@LineEntity
class Product {
    @Column(position = 0, length = 10)
    public String productId;

    @Column(position = 10, length = 21)
    public String name;

    @Column(position = 31, length = 6)
    public Color color;

    @Column(position = 37, length = 10)
    public BigDecimal price;

    @Column(position = 47, length = 10)
    public LocalDate createdDate;

    public Product() {}

    public Product(String productId, String name, Color color, BigDecimal price, LocalDate createdDate) {
        this.productId = productId;
        this.name = name;
        this.color = color;
        this.price = price;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return String.format("Product{id=%s, name=%s, color=%s, price=%s, date=%s}",
                productId, name.trim(), color, price, createdDate);
    }
}

/**
 * Unit tests for complex object type mapping.
 */
public class ComplexTypesMappingTest {
    private LineMapper<Product> mapper;

    @Before
    public void setUp() {
        // Setup registry with custom converters
        TypeConverterRegistry registry = new TypeConverterRegistry();
        registry.register(Color.class, new EnumConverter<>(Color.class));
        registry.register(new LocalDateConverter("yyyyMMdd"));
        
        mapper = new PositionalLineMapper<>(Product.class, registry);
    }

    @Test
    public void testMappingWithEnum() {
        String line = "PROD-001  Widget               RED   100.50    20251203";
        Product product = mapper.toObject(line);

        assertEquals("PROD-001", product.productId);
        assertEquals("Widget", product.name.trim());
        assertEquals(Color.RED, product.color);
    }

    @Test
    public void testMappingWithBigDecimal() {
        String line = "PROD-002  Gadget               BLUE  250.99    20251203";
        Product product = mapper.toObject(line);

        assertEquals(new BigDecimal("250.99"), product.price);
    }

    @Test
    public void testMappingWithLocalDate() {
        String line = "PROD-003  Gizmo                GREEN 50.25     20251210";
        Product product = mapper.toObject(line);

        assertEquals(LocalDate.of(2025, 12, 10), product.createdDate);
    }

    @Test
    public void testRoundTripWithComplexTypes() {
        Product original = new Product(
                "PROD-004",
                "Device",
                Color.YELLOW,
                new BigDecimal("199.99"),
                LocalDate.of(2025, 12, 5)
        );

        String line = mapper.toLine(original);
        Product parsed = mapper.toObject(line);

        assertEquals(original.productId, parsed.productId);
        assertEquals(original.name.trim(), parsed.name.trim());
        assertEquals(original.color, parsed.color);
        assertEquals(original.price, parsed.price);
        assertEquals(original.createdDate, parsed.createdDate);
    }

    @Test
    public void testMultipleProductsWithMixedTypes() {
        java.util.List<String> lines = java.util.Arrays.asList(
                "PROD-005  Item A                RED   100.00    20251201",
                "PROD-006  Item B                BLUE  200.50    20251202",
                "PROD-007  Item C                GREEN 300.25    20251203"
        );

        java.util.List<Product> products = mapper.toObjects(lines);
        assertEquals(3, products.size());
        
        assertEquals(Color.RED, products.get(0).color);
        assertEquals(Color.BLUE, products.get(1).color);
        assertEquals(Color.GREEN, products.get(2).color);

        assertEquals(new BigDecimal("100.00"), products.get(0).price);
        assertEquals(new BigDecimal("300.25"), products.get(2).price);
    }

    @Test
    public void testEnumCaseInsensitivity() {
        // Test with lowercase (should convert to uppercase)
        String line = "PROD-008  Item D                red   150.75    20251204";
        Product product = mapper.toObject(line);
        
        assertEquals(Color.RED, product.color);
    }

    @Test
    public void testBigDecimalPrecision() {
        String line = "PROD-009  Item E                BLUE  99.99     20251205";
        Product product = mapper.toObject(line);
        
        // Verify no rounding errors
        assertTrue(product.price.compareTo(new BigDecimal("99.99")) == 0);
    }
}
