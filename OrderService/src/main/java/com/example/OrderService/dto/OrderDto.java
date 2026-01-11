package com.example.OrderService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long orderId;          // optional, may be null when creating a new order
    private String customerName;
    private String customerEmail;
    private String userId;
    private String status;         // e.g., PENDING, COMPLETED
    private Double totalAmount;
    private String items;          // JSON or comma-separated items
    private LocalDateTime orderDate;

    public void setDriverId(String driverId) {
    }
}
