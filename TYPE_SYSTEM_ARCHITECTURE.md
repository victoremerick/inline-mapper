# Type System Architecture

## Class Hierarchy

```
┌─────────────────────────────────────────────────────────────────┐
│                      TypeConverter<T>                           │
│  ────────────────────────────────────────────────────────────  │
│  + fromString(String): T                                        │
│  + toString(T): String                                          │
│  + getType(): Class<T>                                          │
│  + canHandle(Class): boolean [default]                          │
│  + getName(): String [default]                                  │
└─────────────────────────────────────────────────────────────────┘
              ▲
              │ extends
              │
┌─────────────────────────────────────────────────────────────────┐
│                 AbstractTypeConverter<T>                        │
│  ────────────────────────────────────────────────────────────  │
│  # safeTrim(String): String                                    │
│  + toString(T): String [default impl]                          │
│  + canHandle(Class): boolean [default impl]                    │
│  + getName(): String [default impl]                            │
└─────────────────────────────────────────────────────────────────┘
              ▲
    ┌─────────┴──────────────┬─────────────────┬───────────────┐
    │                        │                 │               │
    │                        │                 │               │
    │                        │                 │               │
Primitive                BigDecimal          LocalDate        UUID
Converters               Converter           Converter        Converter
(String,               (Abstract)           (Abstract)       (Abstract)
Integer,               ─────────────        ─────────────    ─────────
Long,                  fromString()         fromString()     fromString()
Double,                toString()           toString()       toString()
Boolean)
                       └─ Extends AbstractTypeConverter
                          (reuses safeTrim, toString)
```

## Converter Registration & Lookup Flow

```
TypeConverterRegistry
├─ registerDefaultConverters()
│  ├─ StringConverter
│  ├─ IntegerConverter
│  ├─ LongConverter
│  ├─ DoubleConverter
│  ├─ BooleanConverter
│  ├─ BigDecimalConverter
│  ├─ LocalDateConverter
│  └─ UUIDConverter
│
├─ register(TypeConverter<T>)
│  └─ converters.put(type, converter)
│
├─ register(Class<T>, TypeConverter<T>)
│  └─ converters.put(type, converter)  [override]
│
└─ getConverter(Class<T>): TypeConverter<T>
   ├─ Try exact match: converters.get(type)
   ├─ Try assignable: converters.values().stream()
   │  └─ .filter(c -> c.canHandle(type))
   └─ Default to StringConverter
```

## Type Conversion Process

```
Fixed-Width Line
     │
     ▼
Extract Column Substring
     │
     ▼
Apply @Column.trim setting
     │
     ▼
TypeConverterRegistry.getConverter(fieldType)
     │
     ▼
TypeConverter.fromString(value)
     │
     ├─ null/empty handling
     ├─ Format validation
     ├─ Type instantiation
     │
     ▼
Typed Field Value (BigDecimal, LocalDate, Enum, etc)
```

## Entity Parsing Flow with Type System

```
LineMapper.toObject(line: String): T
│
├─ LineEntityMetadata<T>.getColumns()
│  └─ List<ColumnMetadata>
│     ├─ field: Field
│     ├─ column: @Column annotation
│     └─ converter: TypeConverter<?>
│
├─ For each ColumnMetadata:
│  │
│  ├─ extractValue(line, column): String
│  │  └─ line.substring(start, end)
│  │     └─ apply trim if needed
│  │
│  ├─ TypeConverter.fromString(value): Object
│  │  └─ Returns typed value
│  │     (BigDecimal, LocalDate, Enum, etc)
│  │
│  └─ field.set(instance, value)
│     └─ Assigns typed value to field
│
└─ Return T instance
   └─ All fields are strongly typed objects
```

## Entity Serialization Flow with Type System

```
LineMapper.toLine(object: T): String
│
├─ StringBuilder line
├─ currentPosition = 0
│
├─ For each ColumnMetadata:
│  │
│  ├─ field.get(object): Object
│  │  └─ Gets typed value
│  │     (BigDecimal, LocalDate, Enum, etc)
│  │
│  ├─ TypeConverter.toString(typedValue): String
│  │  └─ Converts to string representation
│  │
│  ├─ Pad/truncate to column length
│  │  └─ String.format("%-" + length + "s", str)
│  │
│  └─ line.append(paddedValue)
│     └─ line.position += length
│
└─ Return line.toString()
   └─ Fixed-width string representation
```

## Supported Type Matrix

```
┌──────────────────┬──────────────────┬─────────────┐
│ Type Category    │ Type Names        │ Converter   │
├──────────────────┼──────────────────┼─────────────┤
│ Text             │ String           │ String      │
├──────────────────┼──────────────────┼─────────────┤
│ Integer          │ Integer          │ Integer     │
│                  │ Long             │ Long        │
├──────────────────┼──────────────────┼─────────────┤
│ Decimal          │ Double           │ Double      │
│                  │ BigDecimal       │ BigDecimal  │
├──────────────────┼──────────────────┼─────────────┤
│ Boolean          │ Boolean          │ Boolean     │
├──────────────────┼──────────────────┼─────────────┤
│ Date/Time        │ LocalDate        │ LocalDate   │
│                  │ (Custom format)  │ (Custom)    │
├──────────────────┼──────────────────┼─────────────┤
│ Identifier       │ UUID             │ UUID        │
├──────────────────┼──────────────────┼─────────────┤
│ Enumeration      │ Enum<T>          │ Enum<T>     │
├──────────────────┼──────────────────┼─────────────┤
│ Collection       │ List<String>     │ List        │
├──────────────────┼──────────────────┼─────────────┤
│ Complex          │ @LineEntity      │ Nested      │
│                  │ Custom Classes   │ Custom      │
└──────────────────┴──────────────────┴─────────────┘
```

## Custom Type Implementation Pattern

```
Step 1: Define Type
┌─────────────────────────────────┐
│ public class MyType {           │
│   private final String value;   │
│   public MyType(String s) {...} │
│ }                               │
└─────────────────────────────────┘
         ▼
Step 2: Create Converter
┌────────────────────────────────────────┐
│ public class MyTypeConverter           │
│   extends AbstractTypeConverter<MyType>│
│ {                                      │
│   @Override                            │
│   public MyType fromString(String s) { │
│     return new MyType(safeTrim(s));   │
│   }                                    │
│   @Override                            │
│   public Class<MyType> getType() {    │
│     return MyType.class;              │
│   }                                    │
│ }                                      │
└────────────────────────────────────────┘
         ▼
Step 3: Register
┌──────────────────────────────────────┐
│ TypeConverterRegistry registry = ... │
│ registry.register(new MyTypeConverter)
│ mapper = new PositionalLineMapper<>( │
│   MyClass.class, registry)           │
└──────────────────────────────────────┘
         ▼
Step 4: Use in Entity
┌────────────────────────────────────┐
│ @LineEntity                        │
│ class MyEntity {                   │
│   @Column(position=0, length=20)  │
│   public MyType field;             │
│ }                                  │
└────────────────────────────────────┘
```

## Error Handling Architecture

```
TypeConverter.fromString(value: String): T
│
├─ If value is null
│  └─ Return null
│
├─ If value is empty
│  └─ Return null
│
├─ Try parse/convert
│  │
│  ├─ Success
│  │  └─ Return typed value
│  │
│  └─ Failure
│     └─ Throw Exception
│        └─ (wrapped in MapperException by PositionalLineMapper)
│
└─ MapperException propagated with context
   └─ "Failed to parse line: ..."
      └─ Includes original exception cause
```

## Feature Comparison: Before vs After

```
BEFORE TYPE ENHANCEMENT
══════════════════════════════════════
✓ StringConverter
✓ IntegerConverter
✓ LongConverter
✓ DoubleConverter
✓ BooleanConverter
✗ Date handling
✗ Decimal precision
✗ UUID support
✗ Enum support
✗ Custom types

AFTER TYPE ENHANCEMENT
══════════════════════════════════════
✓ StringConverter          (improved)
✓ IntegerConverter         (improved)
✓ LongConverter            (improved)
✓ DoubleConverter          (improved)
✓ BooleanConverter         (improved)
✓ LocalDateConverter       (new)
✓ BigDecimalConverter      (new)
✓ UUIDConverter            (new)
✓ EnumConverter<T>         (new)
✓ ListConverter            (new)
✓ AbstractTypeConverter    (new base class)
✓ NestedEntityConverter<T> (new)
✓ Custom types support     (new framework)
```

## Inheritance Structure Example

```
TypeConverter<String>
    ▲
    │ implements
    │
StringConverter
    │
    └─ fromString(): String
       └─ return value;
    │
    └─ toString(): String [inherited default]
       └─ return value.toString();

    │
    └─ getType(): Class<String>
       └─ return String.class;


TypeConverter<BigDecimal>
    ▲
    │ implements
    │
AbstractTypeConverter<BigDecimal>
    ▲
    │ extends
    │
BigDecimalConverter
    │
    ├─ fromString(): BigDecimal
    │  └─ String trimmed = safeTrim(value);
    │     return new BigDecimal(trimmed);
    │
    ├─ toString(): String [inherited]
    │  └─ return value.toString();
    │
    └─ getType(): Class<BigDecimal>
       └─ return BigDecimal.class;
```

## Data Flow Diagram

```
                      Input Line
                          │
                          ▼
         ╔════════════════════════════════╗
         ║  PositionalLineMapper.toObject ║
         ╚════════════════════════════════╝
                          │
                          ▼
         ╔════════════════════════════════╗
         ║  LineEntityMetadata.getColumns ║
         ╚════════════════════════════════╝
                          │
                          ▼
         ┌─────────────────────────────┐
         │  For Each ColumnMetadata:   │
         │  - position, length         │
         │  - type, converter          │
         └─────────────────────────────┘
                    ↙        ↘
                   /          \
              Extract        Get
            Substring     Converter
                 │            │
                 ▼            ▼
            Trim Value    TypeConverter
            Substring      Registry
                 │            │
                 └─────┬──────┘
                       │
                       ▼
         ╔════════════════════════════════╗
         ║  TypeConverter.fromString()    ║
         ║  - Parse/validate              ║
         ║  - Convert type                ║
         ║  - Return typed object         ║
         ╚════════════════════════════════╝
                       │
                       ▼
         ╔════════════════════════════════╗
         ║  Set Field on Entity Instance  ║
         ║  - field.set(obj, typedValue)  ║
         ╚════════════════════════════════╝
                       │
                       ▼
         ╔════════════════════════════════╗
         ║  Return Fully Typed Entity     ║
         ║  - All fields properly typed   ║
         ║  - No manual parsing needed    ║
         ╚════════════════════════════════╝
```
