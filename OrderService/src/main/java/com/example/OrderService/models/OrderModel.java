package com.example.OrderService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // Changed from 'id' to 'orderId'

    private String customerName;

    private String customerEmail;

    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    private Double totalAmount;

    private LocalDateTime orderDate;

    // Optional: store a reference to menu items as JSON or another table
    private String items; // e.g., JSON string of ordered items
}
