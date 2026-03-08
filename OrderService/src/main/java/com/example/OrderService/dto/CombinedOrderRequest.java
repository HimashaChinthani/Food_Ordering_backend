package com.example.OrderService.dto;

import lombok.Data;
import java.util.List;

@Data
public class CombinedOrderRequest {

    // user who owns the orders
    private String userId;

    // list of order IDs to combine
    private List<Long> orderIds;

    // payment method (PAYPAL / CARD / CASH)
    private String paymentMethod;

    // total amount for payment
    private Double amount;

}