package com.example.OrderService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PayDTO {

    private Long paymentId;

    private Long orderId;       // FK to OrderModel.orderId

    private String customerName;

    private String customerEmail;

    @JsonProperty("user_id")
    private String userId;

    private String status;

    private Double totalAmount;

    private String items;       // JSON or string

    private LocalDateTime orderDate;
}
