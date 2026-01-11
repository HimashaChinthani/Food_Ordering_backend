package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BillQueueService {

    private final BlockingQueue<OrderDto> customerBillQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<OrderDto> driverBillQueue = new LinkedBlockingQueue<>();

    public void enqueueCustomerBill(OrderDto order) {
        customerBillQueue.add(order);
    }

    public void enqueueDriverBill(OrderDto order) {
        driverBillQueue.add(order);
    }

    public OrderDto getNextCustomerBill() throws InterruptedException {
        return customerBillQueue.take();
    }

    public OrderDto getNextDriverBill() throws InterruptedException {
        return driverBillQueue.take();
    }
}
