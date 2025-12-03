# Quick Reference - Type Converters

## Built-in Converters (Auto-registered)

### Primitives & Basic Types
```java
String         // StringConverter - Pass-through
Integer        // IntegerConverter - Trimmed parsing
Long           // LongConverter - Trimmed parsing
Double         // DoubleConverter - Trimmed parsing
Boolean        // BooleanConverter - "true"|"1" → true, else false
```

### Advanced Types
```java
BigDecimal     // BigDecimalConverter - Exact decimal arithmetic
LocalDate      // LocalDateConverter - ISO format (yyyy-MM-dd) by default
UUID           // UUIDConverter - Standard UUID format
```

## Common Converter Registration Patterns

### Use LocalDate with Custom Format
```java
// Default (yyyy-MM-dd)
TypeConverterRegistry registry = new TypeConverterRegistry();

// Custom format (yyyyMMdd for compact storage)
registry.register(new LocalDateConverter("yyyyMMdd"));

// Other formats
registry.register(new LocalDateConverter("dd/MM/yyyy"));  // European
registry.register(new LocalDateConverter("MM-dd-yyyy"));  // US
```

### Use Enums
```java
public enum Status { ACTIVE, INACTIVE, PENDING }

TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(Status.class, new EnumConverter<>(Status.class));

@LineEntity
class Record {
    @Column(position = 0, length = 8)
    public Status status;  // Automatically converts ACTIVE ↔ "ACTIVE"
}
```

### Use BigDecimal for Money
```java
@LineEntity
class Invoice {
    @Column(position = 0, length = 12)
    public BigDecimal amount;  // No rounding errors
}

// No special setup needed - registered by default
LineMapper<Invoice> mapper = new PositionalLineMapper<>(Invoice.class);
```

### Use UUID for IDs
```java
@LineEntity
class Record {
    @Column(position = 0, length = 36)
    public UUID id;  // UUID format: 550e8400-e29b-41d4-a716-446655440000
}

// No special setup needed - registered by default
LineMapper<Record> mapper = new PositionalLineMapper<>(Record.class);
```

## Creating Custom Converters

### Template: Extend AbstractTypeConverter
```java
public class MyTypeConverter extends AbstractTypeConverter<MyType> {
    @Override
    public MyType fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        if (trimmed == null) return null;
        // TODO: Parse string to MyType
        return new MyType(trimmed);
    }

    @Override
    public Class<MyType> getType() {
        return MyType.class;
    }
}

// Register
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new MyTypeConverter());
```

### Template: For Enums
```java
public enum Priority { LOW, MEDIUM, HIGH }

// Register
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(Priority.class, new EnumConverter<>(Priority.class));

// Use
@LineEntity
class Task {
    @Column(position = 0, length = 6)
    public Priority priority;
}
```

### Template: For Domain Types
```java
public class Email {
    public final String address;
    
    public Email(String address) throws Exception {
        if (!address.contains("@")) throw new Exception("Invalid email");
        this.address = address;
    }
    
    @Override
    public String toString() {
        return address;
    }
}

public class EmailConverter extends AbstractTypeConverter<Email> {
    @Override
    public Email fromString(String value) throws Exception {
        String trimmed = safeTrim(value);
        return trimmed != null ? new Email(trimmed) : null;
    }

    @Override
    public Class<Email> getType() {
        return Email.class;
    }
}
```

## TypeConverter Methods

```java
public interface TypeConverter<T> {
    // Required methods:
    T fromString(String value) throws Exception;
    String toString(T value) throws Exception;
    Class<T> getType();
    
    // Optional (with defaults):
    boolean canHandle(Class<?> type);  // Type matching
    String getName();                   // For debugging
}
```

## Registry Methods

```java
TypeConverterRegistry registry = new TypeConverterRegistry();

// Register a converter
registry.register(new MyConverter());

// Register for specific type (overrides)
registry.register(MyType.class, new MyConverter());

// Get converter
TypeConverter<MyType> conv = registry.getConverter(MyType.class);

// Check existence
boolean exists = registry.hasConverter(MyType.class);

// Count
int count = registry.size();
```

## Complete Working Example

```java
enum OrderStatus { PENDING, SHIPPED, DELIVERED, CANCELLED }

@LineEntity
class Order {
    @Column(position = 0, length = 10)
    public String orderId;
    
    @Column(position = 10, length = 10)
    public LocalDate orderDate;
    
    @Column(position = 20, length = 8)
    public OrderStatus status;
    
    @Column(position = 28, length = 12)
    public BigDecimal total;
}

// Setup
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new LocalDateConverter("yyyyMMdd"));
registry.register(OrderStatus.class, new EnumConverter<>(OrderStatus.class));

// Use
LineMapper<Order> mapper = new PositionalLineMapper<>(Order.class, registry);

String line = "ORD-00001220251203PENDING 1250.50    ";
Order order = mapper.toObject(line);

System.out.println(order.orderId);      // ORD-00001
System.out.println(order.orderDate);    // 2025-12-03 (LocalDate)
System.out.println(order.status);       // PENDING (Enum)
System.out.println(order.total);        // 1250.50 (BigDecimal)
```

## Type Conversion Flow

```
Fixed-Width Line String
         ↓
    Extract substring
         ↓
  Trim (if specified)
         ↓
   TypeConverter.fromString()
         ↓
   Typed Object Field
   
   ════════════════════════
   
   Typed Object Field
         ↓
   TypeConverter.toString()
         ↓
  Pad to column length
         ↓
Fixed-Width Line String
```

## Troubleshooting

### "No converter found for type X"
- Solution 1: Register converter: `registry.register(new MyTypeConverter());`
- Solution 2: Use built-in type instead
- Solution 3: Extend `AbstractTypeConverter` and implement `getType()`

### Parsing fails with Exception
- Check format matches converter expectations
- Verify column length is sufficient for data
- Use `defaultValue` in `@Column` for optional fields
- Add validation to your custom converter

### Null/Empty values
- Converters return `null` by default for empty strings
- Use `@Column(defaultValue="...")` for alternatives
- Check `safeTrim()` usage in custom converters

### Round-trip conversion fails (line ≠ regenerated)
- Verify padding matches original format
- Check field order (should be by position)
- Ensure converters are bidirectional
- Test with exact line lengths

## See Also

- `README.md` - User guide and examples
- `DEVELOPER_GUIDE.md` - Architecture and patterns  
- `TYPE_SYSTEM.md` - Detailed type system documentation
- Test examples: `AdvancedConvertersTest.java`, `ComprehensiveTypesExample.java`
