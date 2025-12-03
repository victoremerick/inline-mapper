package com.example.inlinemapper.example;

import com.example.inlinemapper.annotation.Column;
import com.example.inlinemapper.annotation.LineEntity;
import com.example.inlinemapper.mapper.LineMapper;
import com.example.inlinemapper.mapper.PositionalLineMapper;

import java.util.Arrays;
import java.util.List;

/**
 * Example: Customer Record Processing
 * 
 * This example demonstrates parsing a fixed-width customer file
 * where each line contains customer information in specific positions.
 */
@LineEntity
class CustomerRecord {
    @Column(position = 0, length = 5)
    public String customerId;

    @Column(position = 5, length = 25)
    public String customerName;

    @Column(position = 30, length = 2)
    public Integer status;

    @Column(position = 32, length = 10, defaultValue = "0")
    public Double balance;

    public CustomerRecord() {}

    public CustomerRecord(String customerId, String customerName, Integer status, Double balance) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Customer: %s, Name: %s, Status: %d, Balance: $%.2f",
                customerId, customerName.trim(), status, balance);
    }
}

public class CustomerProcessingExample {
    public static void main(String[] args) {
        LineMapper<CustomerRecord> mapper = new PositionalLineMapper<>(CustomerRecord.class);

        // Sample fixed-width lines (like from a legacy system)
        List<String> inputLines = Arrays.asList(
                "00001John Smith                01 1000.00  ",
                "00002Jane Doe                  02 2500.50  ",
                "00003Bob Johnson               01 500.25   "
        );

        System.out.println("=== Parsing Fixed-Width Lines ===");
        List<CustomerRecord> customers = mapper.toObjects(inputLines);
        for (CustomerRecord customer : customers) {
            System.out.println(customer);
        }

        System.out.println("\n=== Converting Objects Back to Lines ===");
        CustomerRecord newCustomer = new CustomerRecord("00004", "Alice Brown", 2, 3200.75);
        String outputLine = mapper.toLine(newCustomer);
        System.out.println("Length: " + outputLine.length());
        System.out.println("Content: [" + outputLine + "]");

        System.out.println("\n=== Round-trip Conversion ===");
        String line = "00005Charlie White            01 750.00   ";
        CustomerRecord parsed = mapper.toObject(line);
        String regenerated = mapper.toLine(parsed);
        System.out.println("Original:    [" + line + "]");
        System.out.println("Regenerated: [" + regenerated + "]");
    }
}
