package com.example.OrderService.service;


import com.example.OrderService.clients.UserClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.CombinedOrderRequest;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.repo.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserClient userClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;
    public OrderModel createOrder(OrderModel order) {

        // NEW FUNCTIONALITY: validate user
        try {
            userClient.getUserById(order.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("Invalid userId — user not found!");
        }

        return orderRepository.save(order);
    }
    public List<OrderDto> getAllOrders() {
        List<OrderModel> orders = orderRepository.findAll();
        return modelMapper.map(orders, new TypeToken<List<OrderDto>>() {}.getType());
    }
    public OrderDto saveOrder(OrderDto orderDto) {
        OrderModel orderModel = modelMapper.map(orderDto, OrderModel.class);
       OrderModel savedorder = orderRepository.save(orderModel);
        return modelMapper.map(savedorder, OrderDto.class);
    }
    public OrderDto updateOrder(OrderDto orderDto) {
        OrderModel orderModel = modelMapper.map(orderDto, OrderModel.class);
        OrderModel updateorder = orderRepository.save(orderModel);
        return modelMapper.map(updateorder, OrderDto.class);
    }

    public String deleteOrder(Long orderid) {
        if (!orderRepository.existsById(orderid)) {
            throw new RuntimeException("order not found with id: " + orderid);
        }
        orderRepository.deleteById(orderid);
        return "Order Deleted Successfully";
    }
    public List<OrderDto> getOrdersByUserId(String userId) {
        List<OrderModel> orders = orderRepository.findByUserId(userId);
        return modelMapper.map(orders, new TypeToken<List<OrderDto>>() {}.getType());
    }

    public OrderDto updateStatus(Long orderid, String newStatus) {

        OrderModel order = orderRepository.findById(orderid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        OrderModel updated = orderRepository.save(order);

        OrderDto dto = new OrderDto();
        dto.setOrderId(updated.getOrderId());
        dto.setUserId(updated.getUserId());
        dto.setCustomerName(updated.getCustomerName());
        dto.setCustomerEmail(updated.getCustomerEmail());
        dto.setItems(updated.getItems());
        dto.setTotalAmount(updated.getTotalAmount());
        dto.setStatus(updated.getStatus());
        dto.setOrderDate(updated.getOrderDate());

        return dto;
    }

    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    OrderDto dto = new OrderDto();
                    dto.setOrderId(order.getOrderId());
                    dto.setUserId(order.getUserId());
                    dto.setCustomerName(order.getCustomerName());
                    dto.setCustomerEmail(order.getCustomerEmail());
                    dto.setItems(order.getItems());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus());
                    dto.setOrderDate(order.getOrderDate());
                    // deliveryAddress and driverId are not stored on OrderModel currently — leave null
                    return dto;
                })
                .orElse(null);
    }

    // New method: create a combined order from multiple order IDs and mark originals as COMBINED
    public OrderDto createCombinedOrder(CombinedOrderRequest request) {
        if (request == null || request.getOrderIds() == null || request.getOrderIds().isEmpty()) {
            throw new IllegalArgumentException("orderIds required");
        }

        List<Long> ids = request.getOrderIds();

        List<OrderModel> originals = orderRepository.findAllById(ids);
        if (originals.isEmpty()) {
            throw new RuntimeException("No orders found for provided ids");
        }

        // Detect if items look like JSON arrays and collect/merge accordingly
        boolean itemsAreJsonArray = true;
        List<Object> mergedItemsList = new ArrayList<>();
        StringBuilder mergedItemsFallback = new StringBuilder();
        double total = 0d;

        for (OrderModel o : originals) {
            String items = o.getItems();
            if (items == null || items.isEmpty()) {
                // skip
            } else {
                String trimmed = items.trim();
                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    // try parse
                    try {
                        List<Object> part = objectMapper.readValue(trimmed, new TypeReference<List<Object>>() {});
                        mergedItemsList.addAll(part);
                    } catch (Exception ex) {
                        // parsing failed -> fallback
                        itemsAreJsonArray = false;
                        if (mergedItemsFallback.length() > 0) mergedItemsFallback.append(";");
                        mergedItemsFallback.append(items);
                    }
                } else {
                    itemsAreJsonArray = false;
                    if (mergedItemsFallback.length() > 0) mergedItemsFallback.append(";");
                    mergedItemsFallback.append(items);
                }
            }

            if (o.getTotalAmount() != null) {
                total += o.getTotalAmount();
            }
        }

        OrderModel combined = new OrderModel();
        combined.setCustomerName(originals.get(0).getCustomerName());
        combined.setCustomerEmail(originals.get(0).getCustomerEmail());

        if (itemsAreJsonArray) {
            try {
                String combinedJson = objectMapper.writeValueAsString(mergedItemsList);
                combined.setItems(combinedJson);
            } catch (Exception e) {
                combined.setItems(mergedItemsFallback.toString());
            }
        } else {
            combined.setItems(mergedItemsFallback.toString());
        }

        combined.setTotalAmount(total);
        combined.setOrderDate(LocalDateTime.now());
        combined.setStatus("COMPLETED");
        // ensure userId is set so PaidOrderService validation passes
        combined.setUserId(originals.get(0).getUserId());

        OrderModel saved = orderRepository.save(combined);

        // mark originals as COMBINED
        for (OrderModel o : originals) {
            o.setStatus("COMBINED");
        }
        orderRepository.saveAll(originals);

        return modelMapper.map(saved, OrderDto.class);
    }
}
