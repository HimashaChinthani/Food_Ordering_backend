package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import org.springframework.stereotype.Service;
@Service
public class BillService {

    public String generateCustomerBill(OrderDto order) {

        return  "================================\n" +
                "          🍽 FOODIEHUB           \n" +
                "================================\n" +
                "         🧾 CUSTOMER BILL       \n" +
                "--------------------------------\n" +
                " Order ID   : " + order.getOrderId() + "\n" +
                " Customer   : " + order.getCustomerName() + "\n" +
                "--------------------------------\n" +
                " Items\n" +
                "--------------------------------\n" +
                " " + order.getItems() + "\n" +
                "--------------------------------\n" +
                " TOTAL      : $" + order.getTotalAmount() + "\n" +
                "================================\n" +
                "   THANK YOU FOR ORDERING ❤     \n" +
                "        Visit Again!            \n" +
                "================================";
    }

    public String generateDriverBill(OrderDto order) {

        return  "================================\n" +
                "          🍽 FOODIEHUB           \n" +
                "================================\n" +
                "          🚚 DRIVER BILL        \n" +
                "--------------------------------\n" +
                " Order ID   : " + order.getOrderId() + "\n" +
                "--------------------------------\n" +
                " Please deliver safely\n" +
                "================================";
    }
}
