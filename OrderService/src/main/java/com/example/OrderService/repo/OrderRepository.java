package com.example.OrderService.repo;

import com.example.OrderService.models.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    // Find all orders for a specific user
    List<OrderModel> findByUserId(String userId);
}
