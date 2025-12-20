package com.example.OrderService.controllers;

import com.example.OrderService.dto.AssignDriverRequest;
import com.example.OrderService.dto.DriverAssignmentDTO;
import com.example.OrderService.service.DriverAssignmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3")
@RequiredArgsConstructor
@CrossOrigin
public class DriverAssignmentController {

    private final DriverAssignmentService driverAssignmentService;

    @PostMapping("/assigndriver/{orderId}")
    public DriverAssignmentDTO assignDriver(
            @PathVariable Long orderId,
            @RequestBody AssignDriverRequest req) {

        return driverAssignmentService.assignDriver(
                orderId,
                req.getDriverId()
        );
    }

    @GetMapping("/getassigndrivers/{orderId}")
    public DriverAssignmentDTO getAssignedDrivers(@PathVariable Long orderId) {
        return driverAssignmentService.getDriverByOrderId(orderId);
    }


    @DeleteMapping("/unassigndriver/{orderId}")
    public void unAssignDriver(@PathVariable Long orderId) {
        driverAssignmentService.removeDriverAssignment(orderId);
    }
}
