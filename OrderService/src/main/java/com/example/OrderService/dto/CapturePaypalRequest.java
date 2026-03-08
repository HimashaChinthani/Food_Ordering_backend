package com.example.OrderService.dto;

import lombok.Data;
import java.util.List;

@Data
public class CapturePaypalRequest {
    private String orderId;       // PayPal order ID
    private Double amount;
    private Long orderDbId;       // optional single ID
    private List<Long> orderDbIds; // optional multiple IDs
}