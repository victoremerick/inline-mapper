package com.emerick.inlinemapper.mapper;

import com.emerick.inlinemapper.annotation.Column;
import com.emerick.inlinemapper.annotation.LineEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test entity for positional line mapping.
 */
@LineEntity
class Person {
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

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}

/**
 * Unit tests for PositionalLineMapper.
 */
public class PositionalLineMapperTest {
    private LineMapper<Person> mapper;

    @Before
    public void setUp() {
        mapper = new PositionalLineMapper<>(Person.class);
    }

    @Test
    public void testToObjectBasic() {
        String line = "John      25 john@example.com     ";
        Person person = mapper.toObject(line);

        assertEquals("John", person.name);
        assertEquals(Integer.valueOf(25), person.age);
        assertEquals("john@example.com", person.email);
    }

    @Test
    public void testToObjectWithTrimming() {
        String line = "Alice     30 alice@test.com      ";
        Person person = mapper.toObject(line);

        assertEquals("Alice", person.name);
        assertEquals(Integer.valueOf(30), person.age);
        assertEquals("alice@test.com", person.email);
    }

    @Test
    public void testToLineBasic() {
        Person person = new Person("Bob", 35, "bob@example.com");
        String line = mapper.toLine(person);

        assertEquals("Bob       35 bob@example.com     ", line);
    }

    @Test
    public void testToLineWithPadding() {
        Person person = new Person("Charlie", 28, "charlie@test.com");
        String line = mapper.toLine(person);

        assertTrue(line.length() >= 33);
        assertTrue(line.contains("Charlie"));
        assertTrue(line.contains("28"));
        assertTrue(line.contains("charlie@test.com"));
    }

    @Test
    public void testRoundTripConversion() {
        Person original = new Person("Diana", 42, "diana@example.com");
        String line = mapper.toLine(original);
        Person parsed = mapper.toObject(line);

        assertEquals(original.name.trim(), parsed.name.trim());
        assertEquals(original.age, parsed.age);
        assertEquals(original.email.trim(), parsed.email.trim());
    }

    @Test
    public void testToObjects() {
        String line1 = "John      25 john@example.com     ";
        String line2 = "Alice     30 alice@test.com      ";

        java.util.List<String> lines = java.util.Arrays.asList(line1, line2);
        java.util.List<Person> persons = mapper.toObjects(lines);

        assertEquals(2, persons.size());
        assertEquals("John", persons.get(0).name);
        assertEquals("Alice", persons.get(1).name);
    }

    @Test
    public void testToLines() {
        Person person1 = new Person("Bob", 35, "bob@example.com");
        Person person2 = new Person("Eve", 29, "eve@test.com");

        java.util.List<Person> persons = java.util.Arrays.asList(person1, person2);
        java.util.List<String> lines = mapper.toLines(persons);

        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("Bob"));
        assertTrue(lines.get(1).contains("Eve"));
    }

    @Test(expected = MapperException.class)
    public void testToObjectWithInvalidInteger() {
        String line = "John      ABC john@example.com     ";
        mapper.toObject(line);
    }

    @Test
    public void testToObjectWithShortLine() {
        String line = "John      25";
        Person person = mapper.toObject(line);

        assertEquals("John", person.name);
        assertEquals(Integer.valueOf(25), person.age);
    }
}
