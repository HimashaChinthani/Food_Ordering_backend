package com.example.OrderService.controllers;


import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v3")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/getorders")
    public List<OrderDto> getOrders(){
        return orderService
                .getAllOrders();   }

    @PostMapping("/addorder")
    public OrderDto saveOrder(@RequestBody OrderDto OrderDto){
        return orderService.saveOrder(OrderDto);
    }
    @PutMapping("/updateorder")
    public OrderDto updateOrder(@RequestBody OrderDto OrderDto){
        return orderService.updateOrder(OrderDto);
    }
    @DeleteMapping("/deleteorder/{orderid}")
    public String deleteOrder(@PathVariable Long orderid) {
        return orderService.deleteOrder(orderid);
    }



}
