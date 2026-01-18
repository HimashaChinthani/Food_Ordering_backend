package com.example.OrderService.dto;

import lombok.Data;
import java.util.List;

@Data
public class CombinedOrderRequest {
    private List<Long> orderIds;
    private String paymentMethod; // optional
}
