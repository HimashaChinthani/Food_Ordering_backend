package com.example.OrderService.service;

import com.example.OrderService.dto.DriverAssignmentDTO;
import com.example.OrderService.models.DriverAssignmentModel;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.repo.DriverAssignmentRepository;
import com.example.OrderService.repo.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverAssignmentService {

    private final DriverAssignmentRepository driverAssignmentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public DriverAssignmentDTO assignDriver(Long orderId, String driverId) {

        if(driverAssignmentRepository.existsByOrder_OrderId(orderId)) {
            throw new IllegalStateException("Driver already assigned to this order");
        }

        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DriverAssignmentModel dm = new DriverAssignmentModel();
        dm.setOrder(order);
        dm.setDriverId(driverId);
        dm.setStatus("ASSIGNED");
        dm.setAssignedAt(new Date());

        DriverAssignmentModel saved = driverAssignmentRepository.save(dm);

        order.setStatus("DRIVER_ASSIGNED");
        orderRepository.save(order);

        return toDTO(saved);
    }

    public DriverAssignmentDTO getDriverByOrderId(Long orderId) {
        Optional<DriverAssignmentModel> assignmentOpt = driverAssignmentRepository
                .findByOrder_OrderId(orderId);

        if (assignmentOpt.isEmpty()) {
            return null; // or new DriverAssignmentDTO() if you want empty object
        }

        DriverAssignmentModel assignment = assignmentOpt.get();
        DriverAssignmentDTO dto = new DriverAssignmentDTO();
        dto.setId(assignment.getId());
        dto.setDriverId(assignment.getDriverId());
        dto.setOrderId(assignment.getOrder().getOrderId());
        dto.setStatus(assignment.getStatus());
        dto.setAssignedAt(assignment.getAssignedAt());

        return dto;
    }




    @Transactional
    public void removeDriverAssignment(Long orderId) {

        if(!driverAssignmentRepository.existsByOrder_OrderId(orderId)) {
            throw new IllegalArgumentException("No assigned driver for this order");
        }

        driverAssignmentRepository.deleteByOrder_OrderId(orderId);

        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus("COMPLETED");
            orderRepository.save(order);
        });
    }

    private DriverAssignmentDTO toDTO(DriverAssignmentModel model) {
        return new DriverAssignmentDTO(
                model.getId(),
                model.getOrder().getOrderId(),
                model.getDriverId(),
                model.getStatus(),
                model.getAssignedAt()
        );
    }
}

