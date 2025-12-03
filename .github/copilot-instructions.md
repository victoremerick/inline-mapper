## Inline Mapper - Positional Line Mapping Library

This is a Maven-based Java library for mapping positional fixed-width lines to Java objects using annotations. Configured for publishing to Maven Central.

### Project Overview

**Purpose**: Parse and generate fixed-width positional text formats commonly found in:
- Legacy system data exports
- EDI (Electronic Data Interchange) messages
- Mainframe/COBOL data files
- Custom binary-to-text formats

### Key Features

- **Annotation-Based**: Use `@LineEntity` and `@Column` annotations for declarative mapping
- **Bidirectional**: Parse lines to objects and serialize objects back to lines
- **Type Support**: Built-in converters for String, Integer, Long, Double, Boolean
- **Extensible**: Create custom `TypeConverter` implementations for domain types
- **Maven Central Ready**: Fully configured for publishing with GPG signing

### Project Structure

```
inline-mapper/
├── src/main/java/com/example/inlinemapper/
│   ├── annotation/              # @LineEntity, @Column annotations
│   ├── mapper/                  # LineMapper interface & PositionalLineMapper impl
│   │   ├── LineMapper.java
│   │   ├── PositionalLineMapper.java
│   │   ├── LineEntityMetadata.java
│   │   └── ColumnMetadata.java
│   ├── converter/               # Type conversion framework
│   │   ├── TypeConverter.java
│   │   ├── TypeConverterRegistry.java
│   │   └── [StringConverter, IntegerConverter, etc.]
│   └── InlineMapper.java        # Legacy interface (deprecated)
├── src/test/java/com/example/inlinemapper/
│   ├── mapper/PositionalLineMapperTest.java
│   └── example/CustomerProcessingExample.java
├── .github/
│   ├── workflows/
│   │   ├── build.yml            # CI/CD build & test
│   │   └── publish.yml          # Maven Central publishing
│   └── copilot-instructions.md
├── pom.xml                      # Maven configuration
└── README.md                    # Documentation
```

### Core Components

1. **@LineEntity Annotation** - Marks classes for positional mapping
2. **@Column Annotation** - Defines field position and length in lines
3. **LineMapper Interface** - Bidirectional parsing/serialization API
4. **PositionalLineMapper** - Main implementation using reflection and annotations
5. **TypeConverter Framework** - Extensible type conversion system

### Quick Usage Example

```java
@LineEntity
class Person {
    @Column(position = 0, length = 10)
    public String name;
    
    @Column(position = 10, length = 3)
    public Integer age;
}

LineMapper<Person> mapper = new PositionalLineMapper<>(Person.class);
Person p = mapper.toObject("John      25 ");
String line = mapper.toLine(p);
```

### Setup Complete

The project has been scaffolded with:

- ✅ Maven POM configuration for Maven Central publishing
- ✅ Annotation-based positional field mapping
- ✅ Bidirectional line-to-object parsing
- ✅ Type conversion framework with built-in converters
- ✅ GPG signing for artifact verification
- ✅ Source and Javadoc JAR generation
- ✅ Nexus Staging Maven Plugin for automated deployment
- ✅ GitHub Actions CI/CD workflows
- ✅ Comprehensive unit tests and examples

### Next Steps

1. **Update POM metadata** (if needed):
   - Modify `groupId`, `artifactId`, `version`
   - Update project URLs and developer info

2. **Build and Test**:
   ```bash
   mvn clean install
   ```

3. **Add Custom Type Converters**:
   - Implement `TypeConverter<T>` for custom types
   - Register with `TypeConverterRegistry`

4. **Set up GPG and Sonatype** (for publishing):
   - See README.md for detailed instructions

5. **Release to Maven Central**:
   - Create git tag: `git tag -a v1.0.0 -m "Release version 1.0.0"`
   - Push tag: `git push origin v1.0.0`
   - GitHub Actions handles the rest

See **README.md** for comprehensive documentation.
