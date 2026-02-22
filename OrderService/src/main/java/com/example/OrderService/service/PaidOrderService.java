package com.example.OrderService.service;

import com.example.OrderService.clients.UserClient;
import com.example.OrderService.dto.PayDTO;
import com.example.OrderService.dto.UserDTO;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.models.PayOrderModel;
import com.example.OrderService.repo.OrderRepository;
import com.example.OrderService.repo.PaidOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaidOrderService {

    @Autowired
    private PaidOrderRepository paidOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserClient userClient;

    // -------------------- GET ALL --------------------
    public List<PayDTO> getAllOrders() {

        List<PayOrderModel> orders = paidOrderRepository.findAll();

        return orders.stream().map(o -> {

            PayDTO dto = new PayDTO();

            dto.setPaymentId(o.getPaymentId());
            dto.setUserId(o.getUserId());
            dto.setStatus(o.getStatus());
            dto.setTotalAmount(o.getTotalAmount());
            dto.setItems(o.getItems());
            dto.setOrderDate(o.getOrderDate());

            if (o.getOrder() != null) {
                dto.setOrderId(o.getOrder().getOrderId());
                dto.setCustomerName(o.getOrder().getCustomerName());
                dto.setCustomerEmail(o.getOrder().getCustomerEmail());
            }

            return dto;

        }).toList();
    }

    // -------------------- SAVE ORDER --------------------
    public PayDTO saveOrder(PayDTO payDTO) {

        if (payDTO == null || payDTO.getOrderId() == null || payDTO.getUserId() == null) {
            throw new IllegalArgumentException("Required fields are missing");
        }

        // 1️⃣ Validate user via Feign
        ResponseEntity<UserDTO> response;
        try {
            response = userClient.getUserById(payDTO.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Invalid userId — user not found!");
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Invalid userId — user not found!");
        }

        // 2️⃣ Validate order exists
        OrderModel order = orderRepository.findById(payDTO.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found for orderId: "
                                + payDTO.getOrderId()));

        // 3️⃣ Create model (DO NOT set paymentId manually)
        PayOrderModel model = new PayOrderModel();
        model.setOrder(order);
        model.setUserId(payDTO.getUserId());
        model.setStatus(payDTO.getStatus());
        model.setCustomerName(payDTO.getCustomerName());
        model.setCustomerEmail(payDTO.getCustomerEmail());
        model.setTotalAmount(payDTO.getTotalAmount());
        model.setItems(payDTO.getItems());
        model.setOrderDate(payDTO.getOrderDate());

        // 4️⃣ Save
        PayOrderModel saved = paidOrderRepository.save(model);

        // 5️⃣ Convert back to DTO
        PayDTO responseDTO = new PayDTO();
        responseDTO.setPaymentId(saved.getPaymentId());
        responseDTO.setOrderId(saved.getOrder().getOrderId());
        responseDTO.setUserId(saved.getUserId());
        responseDTO.setCustomerName(saved.getCustomerName());
        responseDTO.setCustomerEmail(saved.getCustomerEmail());
        responseDTO.setStatus(saved.getStatus());
        responseDTO.setTotalAmount(saved.getTotalAmount());
        responseDTO.setItems(saved.getItems());
        responseDTO.setOrderDate(saved.getOrderDate());

        return responseDTO;
    }

    // -------------------- DELETE --------------------
    public String deleteOrder(Long paymentId) {
        paidOrderRepository.deleteById(paymentId);
        return "Order deleted successfully!";
    }

    // -------------------- GET BY USER --------------------
    public List<PayDTO> getOrdersByUserId(String userId) {

        List<PayOrderModel> orders = paidOrderRepository.findByUserId(userId);

        return orders.stream().map(o -> {

            PayDTO dto = new PayDTO();

            dto.setPaymentId(o.getPaymentId());
            dto.setUserId(o.getUserId());
            dto.setStatus(o.getStatus());
            dto.setTotalAmount(o.getTotalAmount());
            dto.setItems(o.getItems());
            dto.setOrderDate(o.getOrderDate());

            if (o.getOrder() != null) {
                dto.setOrderId(o.getOrder().getOrderId());
                dto.setCustomerName(o.getOrder().getCustomerName());
                dto.setCustomerEmail(o.getOrder().getCustomerEmail());
            }

            return dto;

        }).toList();
    }
}