package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import org.springframework.stereotype.Service;

@Service
public class BillService {

    public String generateCustomerBill(OrderDto order) {
        return "===== CUSTOMER BILL =====\n" +
                "Order ID: " + order.getOrderId()+ "\n" +
                "Items: " + order.getItems() + "\n" +
                "Total: $" + order.getTotalAmount() + "\n" +
                "Customer: " + order.getCustomerName() + "\n" +
                "=========================";
    }

    public String generateDriverBill(OrderDto order) {
        return "===== DRIVER BILL =====\n" +
                "Order ID: " + order.getOrderId() + "\n" +
               // "Delivery Address: " + order.getDeliveryAddress() + "\n" +
                "=======================";
    }
}
