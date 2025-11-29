package com.example.OrderService.controllers;

import com.example.OrderService.dto.PayDTO;
import com.example.OrderService.service.PaidOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v3/paid-orders")
public class PaidOrderController {

    @Autowired
    private PaidOrderService paidOrderService;

    @GetMapping("/all")
    public List<PayDTO> getOrders() {
        return paidOrderService.getAllOrders();
    }

    @PostMapping("/add")
    public PayDTO saveOrder(@RequestBody PayDTO payDTO) {
        return paidOrderService.saveOrder(payDTO);
    }





    @DeleteMapping("/delete/{paymentId}")
    public String deleteOrder(@PathVariable Long paymentId) {
        return paidOrderService.deleteOrder(paymentId);
    }

    @GetMapping("/user/{userId}")
    public List<PayDTO> getOrdersByUserId(@PathVariable String userId) {
        return paidOrderService.getOrdersByUserId(userId);
    }
}
