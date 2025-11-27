package com.example.OrderService.service;


import com.example.OrderService.clients.UserClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.repo.OrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
