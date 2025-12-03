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
import java.util.Arrays;
import java.util.List;

/**
 * Example: Order Record with Advanced Types
 * 
 * This example demonstrates using custom types (enums, BigDecimal, LocalDate)
 * in a fixed-width positional mapping scenario.
 */
enum OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED
}

@LineEntity
class Order {
    @Column(position = 0, length = 8)
    public String orderId;

    @Column(position = 8, length = 25)
    public String customerName;

    @Column(position = 33, length = 10)
    public LocalDate orderDate;

    @Column(position = 43, length = 3)
    public OrderStatus status;

    @Column(position = 46, length = 12)
    public BigDecimal totalAmount;

    public Order() {}

    public Order(String orderId, String customerName, LocalDate orderDate, 
                 OrderStatus status, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return String.format("Order: %s | Customer: %s | Date: %s | Status: %s | Amount: $%s",
                orderId, customerName.trim(), orderDate, status, totalAmount);
    }
}

public class AdvancedTypesExample {
    public static void main(String[] args) {
        // Create custom converter registry with date and enum converters
        TypeConverterRegistry registry = new TypeConverterRegistry();
        registry.register(new LocalDateConverter("yyyyMMdd"));
        registry.register(OrderStatus.class, new EnumConverter<>(OrderStatus.class));

        LineMapper<Order> mapper = new PositionalLineMapper<>(Order.class, registry);

        System.out.println("=== Parsing Orders with Advanced Types ===");
        
        // Sample fixed-width lines with dates, enums, and BigDecimal
        List<String> inputLines = Arrays.asList(
                "ORD-0001 Acme Corporation        20251201PENDING  1250.50     ",
                "ORD-0002 Tech Industries         20251202COMPLETED2500.75     ",
                "ORD-0003 Global Services        20251203PROCESSING 875.25     "
        );

        List<Order> orders = mapper.toObjects(inputLines);
        for (Order order : orders) {
            System.out.println(order);
        }

        System.out.println("\n=== Converting Objects Back to Fixed-Width Lines ===");
        Order newOrder = new Order(
                "ORD-0004",
                "New Client Ltd",
                LocalDate.of(2025, 12, 4),
                OrderStatus.PENDING,
                new BigDecimal("3250.99")
        );
        
        String outputLine = mapper.toLine(newOrder);
        System.out.println("Generated line length: " + outputLine.length());
        System.out.println("Content: [" + outputLine + "]");

        System.out.println("\n=== Round-trip Conversion ===");
        String line = "ORD-0005 Premium Services       20251205CANCELLED 1500.00     ";
        Order parsed = mapper.toObject(line);
        String regenerated = mapper.toLine(parsed);
        
        System.out.println("Original:    [" + line + "]");
        System.out.println("Regenerated: [" + regenerated + "]");
        System.out.println("Order info: " + parsed);
    }
}
