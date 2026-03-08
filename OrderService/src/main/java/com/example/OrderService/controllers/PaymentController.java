package com.example.OrderService.controllers;

import com.example.OrderService.dto.CapturePaypalRequest;
import com.example.OrderService.dto.CombinedOrderRequest;
import com.example.OrderService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Create PayPal order and return approval URL
    @PostMapping("/paypal/create")
    public String createPaypalOrder(@RequestBody CombinedOrderRequest request) throws Exception {
        try {
            Double amount = request != null ? request.getAmount() : null;
            return paymentService.createPaypalOrder(amount, request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    // Unified POST endpoint: handles both single and multiple order IDs
    @PostMapping("/paypal/capture")
    public String capturePaypalOrder(@RequestBody CapturePaypalRequest request) throws Exception {

        if (request.getOrderId() == null || request.getOrderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }

        List<Long> resolvedOrderDbIds = request.getOrderDbIds();
        if ((resolvedOrderDbIds == null || resolvedOrderDbIds.isEmpty())
                && request.getOrderDbId() != null) {
            resolvedOrderDbIds = Collections.singletonList(request.getOrderDbId());
        }

        if (resolvedOrderDbIds == null || resolvedOrderDbIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderDbId or orderDbIds is required");
        }

        return paymentService.capturePaypalOrder(
                request.getOrderId(),
                request.getAmount(),
                resolvedOrderDbIds
        );
    }

    // GET endpoint for frontend redirect after PayPal approval
    @GetMapping("/paypal/success")
    public RedirectView paypalSuccess(
            @RequestParam String token,
            @RequestParam Double amount,
            @RequestParam(required = false) Long orderDbId,
            @RequestParam(required = false, name = "orderId") Long orderId
    ) {

        Long resolvedOrderId = orderDbId != null ? orderDbId : orderId;
        if (resolvedOrderId == null) {
            return new RedirectView("http://localhost:5173/success?payment=failed&reason=missing_order_id");
        }

        try {
            String status = paymentService.capturePaypalOrder(
                    token,
                    amount,
                    Collections.singletonList(resolvedOrderId)
            );
            return new RedirectView(
                    "http://localhost:5173/success?payment=" + status.toLowerCase() +
                            "&orderDbId=" + resolvedOrderId
            );
        } catch (Exception ex) {
            return new RedirectView("http://localhost:5173/success?payment=failed&reason=capture_error");
        }
    }
}