package com.example.OrderService.repo;

import com.example.OrderService.models.DriverAssignmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAssignmentRepository
        extends JpaRepository<DriverAssignmentModel, Long> {

    // 🔹 Get driver assignment by Order ID (OrderModel's PK is 'orderId')
    Optional<DriverAssignmentModel> findByOrder_OrderId(Long orderId);

    // 🔹 Check if an order already has a driver
    boolean existsByOrder_OrderId(Long orderId);

    // 🔹 Delete assignment by Order ID
    void deleteByOrder_OrderId(Long orderId);
}
