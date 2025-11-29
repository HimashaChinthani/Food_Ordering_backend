package com.example.OrderService.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paid_orders")
public class PayOrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // --- REAL FOREIGN KEY ---
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) // FK column in paid_orders table
    private OrderModel order;  // This maps to OrderModel.orderId

    private String customerName;

    private String customerEmail;

    private String status;        // PENDING, COMPLETED, CANCELLED

    private Double totalAmount;

    private LocalDateTime orderDate;

    private String items;         // JSON or string list

    @Column(name = "user_id")
    private String userId;
}
