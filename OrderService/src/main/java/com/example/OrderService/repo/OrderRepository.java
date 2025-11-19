package com.example.OrderService.repo;

import com.example.OrderService.models.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    // You can add custom query methods if needed
    // Example: List<OrderModel> findByCustomerEmail(String email);
}
