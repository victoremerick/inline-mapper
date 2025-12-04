package com.emerick.inlinemapper.example;

import com.emerick.inlinemapper.annotation.Column;
import com.emerick.inlinemapper.annotation.LineEntity;
import com.emerick.inlinemapper.converter.EnumConverter;
import com.emerick.inlinemapper.converter.LocalDateConverter;
import com.emerick.inlinemapper.converter.TypeConverterRegistry;
import com.emerick.inlinemapper.mapper.LineMapper;
import com.emerick.inlinemapper.mapper.PositionalLineMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive Example: Complete Type System Demonstration
 * 
 * Shows how to use all available type converters:
 * - Primitives and wrappers
 * - Numbers (BigDecimal for financial data)
 * - Dates with custom formats
 * - UUIDs
 * - Enums
 * - Custom types
 */

enum Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum TransactionType {
    DEBIT, CREDIT, TRANSFER, PAYMENT
}

/**
 * Invoice entity with comprehensive type support
 */
@LineEntity
class Invoice {
    @Column(position = 0, length = 12)
    public String invoiceId;

    @Column(position = 12, length = 10)
    public UUID clientId;

    @Column(position = 22, length = 10)
    public LocalDate invoiceDate;

    @Column(position = 32, length = 10)
    public LocalDate dueDate;

    @Column(position = 42, length = 6)
    public TransactionType type;

    @Column(position = 48, length = 12)
    public BigDecimal amount;

    @Column(position = 60, length = 4)
    public Integer itemCount;

    @Column(position = 64, length = 8)
    public Priority priority;

    public Invoice() {}

    public Invoice(String invoiceId, UUID clientId, LocalDate invoiceDate, 
                   LocalDate dueDate, TransactionType type, BigDecimal amount, 
                   Integer itemCount, Priority priority) {
        this.invoiceId = invoiceId;
        this.clientId = clientId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.type = type;
        this.amount = amount;
        this.itemCount = itemCount;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format(
            "Invoice: %s | Client: %s | Date: %s | Due: %s | Type: %s | Amount: $%s | Items: %d | Priority: %s",
            invoiceId, clientId, invoiceDate, dueDate, type, amount, itemCount, priority
        );
    }
}

public class ComprehensiveTypesExample {
    public static void main(String[] args) {
        // Setup registry with all converters
        TypeConverterRegistry registry = new TypeConverterRegistry();
        
        // Register specialized converters
        registry.register(new LocalDateConverter("yyyyMMdd"));
        registry.register(TransactionType.class, new EnumConverter<>(TransactionType.class));
        registry.register(Priority.class, new EnumConverter<>(Priority.class));

        LineMapper<Invoice> mapper = new PositionalLineMapper<>(Invoice.class, registry);

        System.out.println("=== COMPLETE TYPE SYSTEM DEMONSTRATION ===\n");

        // Sample data with all types
        List<String> inputLines = Arrays.asList(
                "INV-202501001 550e8400-e29b-41d4-a716-446655440000 20251201 20260131 DEBIT  1500.50     1000  HIGH    ",
                "INV-202501002 550e8400-e29b-41d4-a716-446655440001 20251202 20260201 CREDIT 2500.75     2500  CRITICAL",
                "INV-202501003 550e8400-e29b-41d4-a716-446655440002 20251203 20260203 TRANSFER 750.25    500  MEDIUM  "
        );

        System.out.println("--- Parsing Invoices with All Types ---");
        List<Invoice> invoices = mapper.toObjects(inputLines);
        for (Invoice invoice : invoices) {
            System.out.println(invoice);
        }

        System.out.println("\n--- Type Details ---");
        Invoice inv = invoices.get(0);
        System.out.println("String:      " + inv.invoiceId + " (type: " + inv.invoiceId.getClass().getSimpleName() + ")");
        System.out.println("UUID:        " + inv.clientId + " (type: " + inv.clientId.getClass().getSimpleName() + ")");
        System.out.println("LocalDate:   " + inv.invoiceDate + " (type: " + inv.invoiceDate.getClass().getSimpleName() + ")");
        System.out.println("BigDecimal:  " + inv.amount + " (type: " + inv.amount.getClass().getSimpleName() + ")");
        System.out.println("Integer:     " + inv.itemCount + " (type: " + inv.itemCount.getClass().getSimpleName() + ")");
        System.out.println("Enum:        " + inv.type + " (type: " + inv.type.getClass().getSimpleName() + ")");

        System.out.println("\n--- Creating New Invoice with All Types ---");
        Invoice newInvoice = new Invoice(
                "INV-202501004",
                UUID.randomUUID(),
                LocalDate.of(2025, 12, 4),
                LocalDate.of(2026, 2, 4),
                TransactionType.PAYMENT,
                new BigDecimal("3250.99"),
                1500,
                Priority.LOW
        );

        String outputLine = mapper.toLine(newInvoice);
        System.out.println("Generated line length: " + outputLine.length() + " chars");
        System.out.println("Content: [" + outputLine + "]");

        System.out.println("\n--- Round-trip Conversion Test ---");
        String originalLine = "INV-202501005 550e8400-e29b-41d4-a716-446655440005 20251205 20260205 TRANSFER 1200.00    1200  MEDIUM  ";
        Invoice parsed = mapper.toObject(originalLine);
        String regenerated = mapper.toLine(parsed);

        System.out.println("Matches after conversion: " + originalLine.equals(regenerated));
        System.out.println("Original:    [" + originalLine + "]");
        System.out.println("Regenerated: [" + regenerated + "]");
        System.out.println("Parsed: " + parsed);

        System.out.println("\n--- Financial Precision Test ---");
        System.out.println("Amount values are exact BigDecimals (no rounding errors):");
        for (Invoice invoice : invoices) {
            System.out.println("  " + invoice.invoiceId + ": " + invoice.amount + 
                             " (precision: " + invoice.amount.scale() + " decimals)");
        }

        System.out.println("\n--- Date Format Flexibility ---");
        System.out.println("Dates stored as: yyyyMMdd (20251201 format)");
        System.out.println("Dates parsed as: LocalDate objects");
        System.out.println("Example: " + invoices.get(0).invoiceDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        System.out.println("\n--- Enum Type Safety ---");
        System.out.println("Enums provide type-safe value constraints:");
        System.out.println("  Priority values: " + String.join(", ", Arrays.stream(Priority.values()).map(Enum::name).toArray(String[]::new)));
        System.out.println("  Transaction types: " + String.join(", ", Arrays.stream(TransactionType.values()).map(Enum::name).toArray(String[]::new)));
    }
}
