package com.example.OrderService.service;

import com.example.OrderService.clients.UserClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.PayDTO;
import com.example.OrderService.dto.UserDTO;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.models.PayOrderModel;
import com.example.OrderService.repo.OrderRepository;
import com.example.OrderService.repo.PaidOrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
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

    @Autowired
    private ModelMapper modelMapper;

    // -------------------- GET ALL --------------------
    public List<PayDTO> getAllOrders() {
        List<PayOrderModel> orders = paidOrderRepository.findAll();
        Type listType = new TypeToken<List<PayDTO>>() {}.getType();
        return modelMapper.map(orders, listType);
    }

    // -------------------- ADD ORDER --------------------
    public PayDTO saveOrder(PayDTO payDTO) {
        // 0️⃣ Validate input
        if (payDTO == null) {
            throw new IllegalArgumentException("PayDTO must not be null");
        }
        if (payDTO.getOrderId() == null) {
            throw new IllegalArgumentException("OrderId must not be null");
        }
        if (payDTO.getUserId() == null) {
            throw new IllegalArgumentException("UserId must not be null");
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
                .orElseThrow(() -> new RuntimeException(
                        "Order not found for orderId: " + payDTO.getOrderId()));

        // 3️⃣ Map DTO -> Model
        PayOrderModel model = new PayOrderModel();
        model.setOrder(order);
        model.setUserId(payDTO.getUserId());
        model.setPaymentId(payDTO.getPaymentId());
        model.setStatus(payDTO.getStatus());
        model.setCustomerName(payDTO.getCustomerName());
        model.setCustomerEmail(payDTO.getCustomerEmail());
        model.setTotalAmount(payDTO.getTotalAmount());
        model.setItems(payDTO.getItems());
        model.setOrderDate(payDTO.getOrderDate());

        // 4️⃣ Save payment
        PayOrderModel saved = paidOrderRepository.save(model);

        // 5️⃣ Map Model -> DTO
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
    // -------------------- UPDATE --------------------


    // -------------------- DELETE --------------------
    public String deleteOrder(Long paymentId) {
        paidOrderRepository.deleteById(paymentId);
        return "Order deleted successfully!";
    }

    // -------------------- GET BY USER --------------------
    public List<PayDTO> getOrdersByUserId(String userId) {
        List<PayOrderModel> orders = paidOrderRepository.findByUserId(userId);
        Type listType = new TypeToken<List<PayDTO>>() {}.getType();
        return modelMapper.map(orders, listType);
    }
}
