# Inline Mapper

A lightweight Java library for mapping positional fixed-width lines to objects and vice versa using annotations.

## Overview

Inline Mapper provides annotation-based mapping between fixed-width positional lines and Java objects. It's designed for parsing and generating formatted text files, EDI messages, legacy data formats, and other positional content.

## Features

- **Annotation-based Mapping**: Use `@LineEntity` and `@Column` annotations to define mappings
- **Bidirectional**: Parse lines to objects and serialize objects back to lines
- **Type Conversion**: Built-in support for String, Integer, Long, Double, and Boolean types
- **File-level Layouts**: Describe full files with `@FileLayout` and `@FileSegment` or via `FileLayout.builder()`, including single lines, fixed ranges, wildcards, and negative indices from file end
- **Extensible**: Create custom type converters for domain-specific types
- **Lightweight**: Minimal dependencies for easy integration
- **Error Handling**: Clear exceptions with detailed error messages

## Annotation Reference

### @LineEntity

Marks a class as a positional line mapped entity.

```java
@LineEntity(separator = "\n")  // Optional separator (default: newline)
public class MyEntity {
    // ...
}
```

### @Column

Marks a field as a positional column.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `position` | int | required | Starting position (0-indexed) |
| `length` | int | required | Column length |
| `defaultValue` | String | "" | Default value if column is empty |
| `trim` | boolean | true | Trim whitespace from extracted value |

```java
@Column(position = 0, length = 10, trim = true)
public String name;

@Column(position = 10, length = 5, defaultValue = "0")
public Integer count;
```

## Supported Types

### Built-in Type Converters

The library includes converters for common types:
- **Primitives & Wrappers**: `String`, `Integer`, `Long`, `Double`, `Boolean`
- **Numbers**: `BigDecimal` (for precise decimal calculations)
- **Dates**: `LocalDate` (with customizable date formats)
- **Identifiers**: `UUID`

All converters handle null/empty values gracefully by returning null.

### Advanced Type Support

#### Enums

Use enums for fixed value sets:

```java
public enum Status {
    ACTIVE, INACTIVE, PENDING
}

@LineEntity
class Order {
    @Column(position = 0, length = 10)
    public Status status;
}

// Register enum converter
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(Status.class, new EnumConverter<>(Status.class));

LineMapper<Order> mapper = new PositionalLineMapper<>(Order.class, registry);
```

#### LocalDate with Custom Formats

Parse dates in any format:

```java
// Default format (yyyy-MM-dd)
TypeConverterRegistry registry = new TypeConverterRegistry();

// Custom format (yyyyMMdd - without separators)
registry.register(new LocalDateConverter("yyyyMMdd"));

LineMapper<Order> mapper = new PositionalLineMapper<>(Order.class, registry);
```

#### BigDecimal

For precise financial calculations:

```java
@LineEntity
class Invoice {
    @Column(position = 0, length = 10)
    public String invoiceId;
    
    @Column(position = 10, length = 12)
    public BigDecimal amount;  // No rounding errors
}

LineMapper<Invoice> mapper = new PositionalLineMapper<>(Invoice.class);
```

#### UUID

For unique identifiers:

```java
@LineEntity
class Record {
    @Column(position = 0, length = 36)
    public UUID id;
}
```

### Custom Type Converters

Create converters for domain-specific types by extending `AbstractTypeConverter`:

```java
import converter.com.emerick.inlinemapper.AbstractTypeConverter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthConverter extends AbstractTypeConverter<YearMonth> {
   private static final DateTimeFormatter FORMATTER =
           DateTimeFormatter.ofPattern("yyyyMM");

   @Override
   public YearMonth fromString(String value) throws Exception {
      String trimmed = safeTrim(value);
      return trimmed != null ? YearMonth.parse(trimmed, FORMATTER) : null;
   }

   @Override
   public Class<YearMonth> getType() {
      return YearMonth.class;
   }
}
```

Register and use:

```java
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(new YearMonthConverter());

LineMapper<MyEntity> mapper = new PositionalLineMapper<>(MyEntity.class, registry);
```

#### Abstract Base Class Benefits

`AbstractTypeConverter` provides:
- Default `toString()` implementation
- `safeTrim()` helper method for null-safe trimming
- `canHandle()` type checking
- `getName()` for debugging

### Custom Type Examples

#### Money/Currency Type

```java
public class Money {
    private final BigDecimal amount;
    private final String currency;
    
    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public static Money of(String value) throws Exception {
        // Parse "USD1234.56" format
        String currency = value.substring(0, 3);
        BigDecimal amount = new BigDecimal(value.substring(3));
        return new Money(amount, currency);
    }
    
    @Override
    public String toString() {
        return currency + amount;
    }
}

public class MoneyConverter extends AbstractTypeConverter<Money> {
    @Override
    public Money fromString(String value) throws Exception {
        return Money.of(value);
    }

    @Override
    public Class<Money> getType() {
        return Money.class;
    }
}
```

#### Country Code Type

```java
public enum CountryCode {
    US, CA, BR, DE, FR, UK, JP, CN;
}

// Use EnumConverter
TypeConverterRegistry registry = new TypeConverterRegistry();
registry.register(CountryCode.class, new EnumConverter<>(CountryCode.class));
```

### File-Level Layouts (Multiple Lines)

Describe an entire file with annotations or builder syntax, supporting ranges, wildcards, and negative indices (from the end):

```java
import annotation.com.emerick.inlinemapper.FileLayout;
import annotation.com.emerick.inlinemapper.FileSegment;
import mapper.com.emerick.inlinemapper.FileLayoutBuilder;
import mapper.com.emerick.inlinemapper.FileMapper;
import mapper.com.emerick.inlinemapper.FileMappingResult;

@FileLayout
class MyFileLayout {
   @FileSegment(position = 1)
   HeaderLine header;
   @FileSegment(position = 2)
   InfoLine info;
   @FileSegment(wildcard = true)
   List<DetailLine> details; // consumes lines until next anchored segment
   @FileSegment(position = -2)
   TrailerLine trailer;        // second-to-last line
   @FileSegment(position = -1)
   FooterLine footer;          // last line
}

FileMapper mapper = new FileMapper(FileLayoutBuilder.fromAnnotations(MyFileLayout.class));
FileMappingResult result = mapper.map(lines);
HeaderLine header = result.getSingle("header", HeaderLine.class);
List<DetailLine> detailLines = result.getList("details", DetailLine.class);
```

Or build programmatically:

```java
import mapper.com.emerick.inlinemapper.FileLayout;
import mapper.com.emerick.inlinemapper.FileMapper;
import mapper.com.emerick.inlinemapper.FileMappingResult;

FileLayout layout = FileLayout.builder()
        .line("header", 1, HeaderLine.class)
        .line("info", 2, InfoLine.class)
        .wildcard("details", DetailLine.class) // variable-length section
        .line("trailer", -2, TrailerLine.class)
        .line("footer", -1, FooterLine.class)
        .build();

FileMapper mapper = new FileMapper(layout);
FileMappingResult result = mapper.map(lines);
```

## Quick Start

### Define a Mapped Entity

```java
import annotation.com.emerick.inlinemapper.LineEntity;
import annotation.com.emerick.inlinemapper.Column;

@LineEntity
public class Person {
   @Column(position = 0, length = 10)
   public String name;

   @Column(position = 10, length = 3)
   public Integer age;

   @Column(position = 13, length = 20)
   public String email;

   public Person() {
   }

   public Person(String name, Integer age, String email) {
      this.name = name;
      this.age = age;
      this.email = email;
   }
}
```

### Parse Lines to Objects

```java
import mapper.com.emerick.inlinemapper.LineMapper;
import mapper.com.emerick.inlinemapper.PositionalLineMapper;

public class Example {
   public static void main(String[] args) {
      LineMapper<Person> mapper = new PositionalLineMapper<>(Person.class);

      // Parse a fixed-width line
      String line = "John      25 john@example.com     ";
      Person person = mapper.toObject(line);

      System.out.println(person.name);  // John
      System.out.println(person.age);   // 25
      System.out.println(person.email); // john@example.com
   }
}
```

### Serialize Objects to Lines

```java
Person person = new Person("Jane", 30, "jane@example.com");
String line = mapper.toLine(person);
System.out.println(line);  // Jane      30 jane@example.com     
```

### Batch Operations

```java
// Parse multiple lines
List<String> lines = Arrays.asList(
    "John      25 john@example.com     ",
    "Jane      30 jane@example.com     "
);
List<Person> persons = mapper.toObjects(lines);

// Serialize multiple objects
List<String> outputLines = mapper.toLines(persons);
```

## Building

### Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher

### Build

```bash
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Build Package

```bash
mvn package
```

## Installation

### Maven

Add this to your `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>inline-mapper</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add this to your `build.gradle`:

```gradle
implementation 'com.example:inline-mapper:1.0.0'
```

## Publishing to Maven Central

### Prerequisites

1. Create a Sonatype JIRA account at https://issues.sonatype.org/
2. Create a GPG key pair for signing artifacts
3. Publish your GPG public key to a key server

### Setup

1. **Create settings.xml**:
   ```bash
   cp settings.xml.template ~/.m2/settings.xml
   ```

2. **Update credentials**:
   - Replace `YOUR_SONATYPE_USERNAME` with your Sonatype username
   - Replace `YOUR_SONATYPE_PASSWORD` with your Sonatype password
   - Replace `YOUR_GPG_PASSPHRASE` with your GPG passphrase

3. **Configure GitHub Secrets** (for CI/CD):
   - `SONATYPE_USERNAME`: Your Sonatype username
   - `SONATYPE_PASSWORD`: Your Sonatype password
   - `GPG_PRIVATE_KEY`: Your GPG private key (exported as ASCII)
   - `GPG_PASSPHRASE`: Your GPG passphrase

### Deploy Snapshot

```bash
mvn clean deploy
```

### Release to Maven Central

1. Tag your release:
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

2. The GitHub Actions workflow will automatically:
   - Build and test your code
   - Sign artifacts with GPG
   - Deploy to Sonatype OSS Maven Repository
   - Close and release the staging repository

3. The artifact will appear on Maven Central within 30 minutes to 2 hours.

## Project Structure

```
inline-mapper/
├── src/
│   ├── main/java/com/example/inlinemapper/
│   │   ├── InlineMapper.java          # Main interface
│   │   └── DefaultInlineMapper.java    # Default implementation
│   └── test/java/com/example/inlinemapper/
│       └── DefaultInlineMapperTest.java # Unit tests
├── .github/workflows/
│   ├── build.yml     # CI/CD build workflow
│   └── publish.yml   # Maven Central publishing workflow
├── pom.xml           # Maven configuration
└── README.md         # This file
```

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6.0 or higher

## Dependencies

### Runtime

- None (zero dependencies)

### Test

- JUnit 4.13.2

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## Troubleshooting

### GPG Issues

If you encounter GPG-related errors during deployment:

1. Ensure your GPG key is properly configured:
   ```bash
   gpg --list-secret-keys
   ```

2. If using GitHub Actions, ensure your private key is exported in ASCII format:
   ```bash
   gpg --export-secret-keys --armor YOUR_KEY_ID
   ```

### Maven Central Sync Issues

- Check Sonatype's repository status: https://oss.sonatype.org
- View sync logs in your Sonatype account
- Artifacts may take 30 minutes to 2 hours to appear on Maven Central

## Support

For issues, questions, or suggestions, please open an issue on GitHub.

---

**Note**: Replace placeholder values in configuration files with your actual credentials and information.
