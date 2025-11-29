package com.example.OrderService.repo;

import com.example.OrderService.models.PayOrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaidOrderRepository extends JpaRepository<PayOrderModel, Long> {

    List<PayOrderModel> findByUserId(String userId);
}
