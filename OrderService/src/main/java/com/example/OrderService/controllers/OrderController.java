package com.example.OrderService.controllers;


import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.CombinedOrderRequest;
import com.example.OrderService.dto.PayDTO;
import com.example.OrderService.service.OrderService;
import com.example.OrderService.service.PaidOrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v3")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaidOrderService paidOrderService;

    @Autowired
    private ObjectMapper objectMapper;

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
            List<Long> ids = new ArrayList<>();

            if (payload.isArray()) {
                for (JsonNode el : payload) {
                    if (el.isNumber()) {
                        ids.add(el.asLong());
                    } else if (el.has("orderId")) {
                        ids.add(el.get("orderId").asLong());
                    } else if (el.has("id")) {
                        ids.add(el.get("id").asLong());
                    } else {
                        // try to read as number text
                        try {
                            ids.add(Long.parseLong(el.asText()));
                        } catch (Exception ex) {
                            return ResponseEntity.badRequest().body("Unable to parse array element to orderId: " + el.toString());
                        }
                    }
                }
            } else if (payload.has("orderIds") && payload.get("orderIds").isArray()) {
                for (JsonNode el : payload.get("orderIds")) {
                    ids.add(el.asLong());
                }
            } else if (payload.has("orderId")) {
                ids.add(payload.get("orderId").asLong());
            } else if (payload.has("id")) {
                ids.add(payload.get("id").asLong());
            } else if (payload.isNumber()) {
                ids.add(payload.asLong());
            } else {
                return ResponseEntity.badRequest().body("Invalid payload for pay-multiple: must be orderIds array, array of order objects, or single orderId/order object");
            }

            CombinedOrderRequest req = new CombinedOrderRequest();
            req.setOrderIds(ids);
            if (payload.has("paymentMethod")) {
                req.setPaymentMethod(payload.get("paymentMethod").asText(null));
            }

            // create combined order
            OrderDto combined = orderService.createCombinedOrder(req);

            // build PayDTO to persist payment record
            PayDTO payDTO = new PayDTO();
            payDTO.setOrderId(combined.getOrderId());
            payDTO.setUserId(combined.getUserId());
            payDTO.setCustomerName(combined.getCustomerName());
            payDTO.setCustomerEmail(combined.getCustomerEmail());
            payDTO.setItems(combined.getItems());
            payDTO.setTotalAmount(combined.getTotalAmount());
            payDTO.setStatus("COMPLETED");
            payDTO.setOrderDate(combined.getOrderDate());

            PayDTO savedPayment = paidOrderService.saveOrder(payDTO);

            return ResponseEntity.ok(savedPayment);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("pay-multiple failed: " + e.getMessage());
        }
    }


}
