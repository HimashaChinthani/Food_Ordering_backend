package com.example.OrderService.controllers;

import com.example.OrderService.dto.AssignDriverRequest;
import com.example.OrderService.dto.DriverAssignmentDTO;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.service.BillQueueService;
import com.example.OrderService.service.BillService;
import com.example.OrderService.service.DriverAssignmentService;
import com.example.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3")
@RequiredArgsConstructor
@CrossOrigin
public class DriverAssignmentController {

    private final DriverAssignmentService driverAssignmentService;
    private final BillQueueService billQueueService;
    private final OrderService orderService;
    private final BillService billService;

    // ------------------- Assign Driver -------------------
    @PostMapping("/assigndriver/{orderId}")
    public DriverAssignmentDTO assignDriver(
            @PathVariable Long orderId,
            @RequestBody AssignDriverRequest req) {

        // 1. Assign driver via existing service
        DriverAssignmentDTO assignmentDTO = driverAssignmentService.assignDriver(
                orderId,
                String.valueOf(req.getDriverId())
        );

        // 2. Fetch full order details
        OrderDto order = orderService.getOrderById(orderId);
        if (order != null) {
            // Set driver ID in order DTO
            order.setDriverId(req.getDriverId());

            // 3. Add bills to queue
            billQueueService.enqueueCustomerBill(order);
            billQueueService.enqueueDriverBill(order);
        }

        return assignmentDTO;
    }

    // ------------------- Get Assigned Driver -------------------
    @GetMapping("/getassigndrivers/{orderId}")
    public DriverAssignmentDTO getAssignedDrivers(@PathVariable Long orderId) {
        return driverAssignmentService.getDriverByOrderId(orderId);
    }

    // ------------------- Unassign Driver -------------------
    @DeleteMapping("/unassigndriver/{orderId}")
    public void unAssignDriver(@PathVariable Long orderId) {
        driverAssignmentService.removeDriverAssignment(orderId);
    }

    // ------------------- Optional: Get Bill by Type -------------------
    // Frontend can call this to fetch and print the bill
    @GetMapping("/bill/{orderId}/{type}")
    public String getBill(@PathVariable Long orderId, @PathVariable String type) {
        OrderDto order = orderService.getOrderById(orderId);
        if (order == null) return "Order not found";

        if ("customer".equalsIgnoreCase(type)) {
            return billService.generateCustomerBill(order);
        } else if ("driver".equalsIgnoreCase(type)) {
            return billService.generateDriverBill(order);
        } else {
            return "Invalid bill type";
        }
    }
}
