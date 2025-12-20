package com.example.OrderService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(
        name = "driver_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id"})
        }
)
public class DriverAssignmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SAME SERVICE → real FK
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;

    // OTHER SERVICE → ID only
    @Column(name = "driver_id", nullable = false)
    private String driverId;

    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedAt;
}
