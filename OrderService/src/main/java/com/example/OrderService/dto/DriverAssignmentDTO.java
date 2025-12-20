package com.example.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAssignmentDTO {
    // fields used by the service to construct DTO
    private Long id;
    private Long orderId;
    private String driverId;
    private String status;
    private Date assignedAt;

    // additional driver details (may be populated by calling UserService)
    private String driverName;
    private String driverEmail;
    private String driverPhoneNumber;
    private String driverVehicleNumber;
    private String driverVehicleType;

    // include some order fields for convenience
    private Double totalAmount;
    private String customerName;
    private String customerEmail;

    // explicit 5-arg constructor used in the service
    public DriverAssignmentDTO(Long id, Long orderId, String driverId, String status, Date assignedAt) {
        this.id = id;
        this.orderId = orderId;
        this.driverId = driverId;
        this.status = status;
        this.assignedAt = assignedAt;
    }
}
