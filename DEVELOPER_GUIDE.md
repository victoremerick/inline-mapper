# Inline Mapper - Developer Guide

## Type System Overview

Inline Mapper supports extensive type conversions:

**Basic Types**: String, Integer, Long, Double, Boolean

**Advanced Types**: 
- `BigDecimal` - Precise decimal arithmetic without rounding
- `LocalDate` - Date handling with customizable formats
- `UUID` - Unique identifiers
- `Enum` - Fixed value sets with safe conversion
- Custom types via `TypeConverter` interface

**Composition**:
- Nested `@LineEntity` objects
- Collections (comma-separated lists)
- Domain-specific types (Money, CountryCode, etc)

## Project Structure

```
src/main/java/com/example/inlinemapper/
├── annotation/
│   ├── LineEntity.java      # @LineEntity - marks classes for positional mapping
│   └── Column.java          # @Column - marks fields and their positions
├── mapper/
│   ├── LineMapper.java      # Interface for bidirectional line mapping
│   ├── PositionalLineMapper.java  # Main implementation
│   ├── LineEntityMetadata.java    # Metadata about @LineEntity classes
│   ├── ColumnMetadata.java        # Metadata about @Column fields
│   └── MapperException.java       # Exception for mapping errors
├── converter/
│   ├── TypeConverter.java    # Interface for type conversion
│   ├── TypeConverterRegistry.java  # Registry for converters
│   ├── StringConverter.java
│   ├── IntegerConverter.java
│   ├── LongConverter.java
│   ├── DoubleConverter.java
│   └── BooleanConverter.java
└── InlineMapper.java        # Legacy interface (deprecated)
```

## Usage Patterns

### Basic Mapping

```java
@LineEntity
class Product {
    @Column(position = 0, length = 10)
    public String productId;
    
    @Column(position = 10, length = 30)
    public String name;
    
    @Column(position = 40, length = 8)
    public Double price;
}

LineMapper<Product> mapper = new PositionalLineMapper<>(Product.class);
Product p = mapper.toObject("PROD-001  Widget                     99.99    ");
```

### Custom Type Converters

```java
public class LocalDateConverter implements TypeConverter<LocalDate> {
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd");
    
    @Override
    public LocalDate fromString(String value) throws Exception {
        return LocalDate.parse(value, FORMATTER);
    }
    
    @Override
    public String toString(LocalDate value) throws Exception {
        return value.format(FORMATTER);
    }
    
    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }
}

TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new LocalDateConverter());
mapper = new PositionalLineMapper<>(MyEntity.class, registry);
```

### Batch Processing

```java
List<String> lines = Files.readAllLines(Paths.get("data.txt"));
List<Product> products = mapper.toObjects(lines);

// Process products...

List<String> output = mapper.toLines(products);
Files.write(Paths.get("output.txt"), output);
```

## Key Classes

### LineMapper<T> Interface

Provides bidirectional mapping between lines and objects:
- `T toObject(String line)` - Parse line to object
- `String toLine(T object)` - Serialize object to line
- `List<T> toObjects(List<String> lines)` - Parse multiple lines
- `List<String> toLines(List<T> objects)` - Serialize multiple objects

### PositionalLineMapper<T>

Implementation of LineMapper using reflection and annotations.

```java
// Basic usage
LineMapper<T> mapper = new PositionalLineMapper<>(T.class);

// With custom converters
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new CustomConverter());
LineMapper<T> mapper = new PositionalLineMapper<>(T.class, registry);
```

### TypeConverter<T> Interface & AbstractTypeConverter<T>

Implement for custom type conversions:
- `T fromString(String value)` - Convert from string
- `String toString(T value)` - Convert to string
- `Class<T> getType()` - Return the handled type
- `boolean canHandle(Class<?> type)` - Optional type matching
- `String getName()` - Optional friendly name

Use `AbstractTypeConverter<T>` as base class for convenient implementation:

```java
public class CustomTypeConverter extends AbstractTypeConverter<CustomType> {
    @Override
    public CustomType fromString(String value) throws Exception {
        String trimmed = safeTrim(value);  // Null-safe trim
        if (trimmed == null) return null;
        // ... conversion logic
        return new CustomType(trimmed);
    }

    @Override
    public Class<CustomType> getType() {
        return CustomType.class;
    }
}
```

### TypeConverterRegistry

Manages type converters with automatic registration and lookup:

```java
TypeConverterRegistry registry = new TypeConverterRegistry();

// Register custom converter
registry.register(new CustomConverter());

// Register for specific type
registry.register(MyEnum.class, new EnumConverter<>(MyEnum.class));

// Check if converter exists
if (registry.hasConverter(LocalDate.class)) {
    TypeConverter<LocalDate> converter = registry.getConverter(LocalDate.class);
}
```

**Built-in converters** automatically registered:
- Primitives: String, Integer, Long, Double, Boolean
- Numbers: BigDecimal
- Dates: LocalDate (ISO format by default)
- IDs: UUID

### Specialized Converters

**EnumConverter** - For enum types:
```java
registry.register(Status.class, new EnumConverter<>(Status.class));
```

**LocalDateConverter** - With custom format:
```java
registry.register(new LocalDateConverter("yyyyMMdd"));  // 20251203 format
```

**NestedEntityConverter** - For nested @LineEntity objects (advanced):
```java
registry.register(new NestedEntityConverter<>(AddressEntity.class, registry));
```

**ListConverter** - For comma-separated lists:
```java
TypeConverter<List<String>> converter = new ListConverter(",");
```

## Column Positioning

Column positions are 0-indexed and define where fields appear in the fixed-width line:

```
Position: 0         1         2         3
          0123456789012345678901234567890123456789
Content:  PROD-001  Widget                     99.99
          ^-------^ ^----^    ^---^             ^----^
          Position  Position  Position         Position
          0(10)     10(30)    40(8)            48(8)
```

## Error Handling

All mapping operations throw `MapperException` on failure:

```java
try {
    Product p = mapper.toObject(malformedLine);
} catch (MapperException e) {
    System.err.println("Failed to parse: " + e.getMessage());
    // Handle error
}
```

## Testing

Unit tests are located in `src/test/java/com/example/inlinemapper/`:
- `mapper/PositionalLineMapperTest.java` - Core mapping tests
- `example/CustomerProcessingExample.java` - Usage example

Run tests:
```bash
mvn test
```

## Maven Central Publishing

See README.md for detailed publishing instructions:
1. Update POM metadata
2. Set up GPG signing
3. Configure Sonatype credentials
4. Configure GitHub Actions secrets
5. Create a git tag and push to trigger automated publishing

## Common Issues

### Issue: NullPointerException when getting field value

**Cause**: Field is not accessible or not initialized

**Solution**: Ensure field is public or has proper getters, and objects are properly constructed

### Issue: Parsing fails with NumberFormatException

**Cause**: Non-numeric value in numeric column

**Solution**: Check input data format, use `defaultValue` if column may be empty

### Issue: Output line is shorter/longer than expected

**Cause**: Positions don't align or text is truncated/padded incorrectly

**Solution**: Verify column positions and lengths, use calculator tool to map positions correctly
