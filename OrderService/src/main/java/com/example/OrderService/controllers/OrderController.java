package com.example.OrderService.controllers;


import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.CombinedOrderRequest;
import com.example.OrderService.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @GetMapping("/getorders/{userId}")
    public List<OrderDto> getOrdersByUserId(@PathVariable String userId) {
        return orderService.getOrdersByUserId(userId);
    }
    @PutMapping("/updatestatus/{orderid}")
    public OrderDto updateStatus(@PathVariable Long orderid) {
        return orderService.updateStatus(orderid, "COMPLETED");
    }
    @PostMapping("/pay-multiple")
    public ResponseEntity<?> payMultipleAndCombine(@RequestBody JsonNode payload) {
        try {
            // Step 1: Extract order IDs
            Set<Long> ids = new LinkedHashSet<>(); // remove duplicates

            if (payload.isArray()) {
                for (JsonNode el : payload) {
                    if (el.isNumber()) ids.add(el.asLong());
                    else if (el.has("orderId")) ids.add(el.get("orderId").asLong());
                    else if (el.has("id")) ids.add(el.get("id").asLong());
                    else {
                        try { ids.add(Long.parseLong(el.asText())); }
                        catch (Exception ex) {
                            return ResponseEntity.badRequest()
                                    .body("Unable to parse array element to orderId: " + el.toString());
                        }
                    }
                }
            } else if (payload.has("orderIds") && payload.get("orderIds").isArray()) {
                for (JsonNode el : payload.get("orderIds")) ids.add(el.asLong());
            } else if (payload.has("orderId") && !payload.has("payAll")) {
                ids.add(payload.get("orderId").asLong());
            } else if (payload.has("id") && !payload.has("payAll")) {
                ids.add(payload.get("id").asLong());
            } else if (payload.isNumber()) {
                ids.add(payload.asLong());
            }

            // Fallback mode for Pay All button: send userId (and optionally payAll=true)
            if (ids.isEmpty() && payload.has("userId")) {
                String userId = payload.get("userId").asText();
                List<OrderDto> userOrders = orderService.getOrdersByUserId(userId);
                userOrders.stream()
                        .filter(o -> o.getOrderId() != null)
                        .filter(o -> "PENDING".equalsIgnoreCase(o.getStatus()))
                        .map(OrderDto::getOrderId)
                        .forEach(ids::add);
            }

            if (ids.isEmpty()) return ResponseEntity.badRequest().body("No valid PENDING order IDs provided");

            // Step 2: Create CombinedOrderRequest
            CombinedOrderRequest req = new CombinedOrderRequest();
            req.setOrderIds(new ArrayList<>(ids));
            if (payload.has("paymentMethod")) req.setPaymentMethod(payload.get("paymentMethod").asText(null));

            // Step 3: Create combined order (payment happens later via PayPal capture)
            OrderDto combined = orderService.createCombinedOrder(req);
            return ResponseEntity.ok(combined);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("pay-multiple failed: " + e.getMessage());
        }
    }


}
