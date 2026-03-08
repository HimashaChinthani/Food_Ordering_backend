package com.example.OrderService.service;

import com.example.OrderService.dto.CombinedOrderRequest;
import com.example.OrderService.models.OrderModel;
import com.example.OrderService.models.PayOrderModel;
import com.example.OrderService.models.Payment;
import com.example.OrderService.repo.OrderRepository;
import com.example.OrderService.repo.PaidOrderRepository;
import com.example.OrderService.repo.PaymentRepository;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PayPalHttpClient payPalClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaidOrderRepository paidOrderRepository;

    @Autowired
    private OrderService orderService;


    // ===============================
    // CREATE PAYPAL ORDER
    // ===============================
    public String createPaypalOrder(Double amount, CombinedOrderRequest combinedRequest) throws Exception {

        Double resolvedAmount = amount;
        Long resolvedOrderDbId = null;

        // If this is a combined checkout, trust the server-calculated total.
        if (combinedRequest != null &&
            combinedRequest.getOrderIds() != null &&
            !combinedRequest.getOrderIds().isEmpty()) {

            var combinedOrder = orderService.createCombinedOrder(combinedRequest);
            System.out.println("Combined order created: " + combinedOrder.getOrderId());
            resolvedAmount = combinedOrder.getTotalAmount();
            resolvedOrderDbId = combinedOrder.getOrderId();
        }

        if (resolvedOrderDbId == null &&
            combinedRequest != null &&
            combinedRequest.getOrderIds() != null &&
            combinedRequest.getOrderIds().size() == 1) {
            resolvedOrderDbId = combinedRequest.getOrderIds().get(0);
        }

        if (resolvedAmount == null || resolvedAmount <= 0) {
            throw new IllegalArgumentException("Valid amount is required");
        }

        OrdersCreateRequest request = new OrdersCreateRequest();
        request.header("prefer", "return=representation");

        request.requestBody(buildRequestBody(resolvedAmount, resolvedOrderDbId));

        var response = payPalClient.execute(request);

        for (LinkDescription link : response.result().links()) {
            if (link.rel().equals("approve")) {

                // return approval URL
                return link.href();
            }
        }

        return null;
    }


    // ===============================
    // CAPTURE PAYPAL PAYMENT
    // ===============================

    @Transactional
    public String capturePaypalOrder(String paypalOrderId, Double amount, List<Long> orderDbIds) throws Exception {

        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.requestBody(new OrderActionRequest());
        var response = payPalClient.execute(request);

        String status = response.result().status();

        if ("COMPLETED".equals(status)) {

            String transactionId = response.result()
                    .purchaseUnits()
                    .get(0)
                    .payments()
                    .captures()
                    .get(0)
                    .id();

            // Loop over all order IDs
            for (Long orderDbId : orderDbIds) {

                OrderModel order = orderRepository.findById(orderDbId)
                        .orElseThrow(() -> new RuntimeException("Order not found: " + orderDbId));

                order.setStatus("COMPLETED");
                orderRepository.save(order);

                Payment payment = new Payment();
                payment.setAmount(order.getTotalAmount());
                payment.setOrderId(orderDbId);
                payment.setStatus(status);
                payment.setTransactionId(transactionId);
                paymentRepository.save(payment);

                // Save to paid_orders
                if (!paidOrderRepository.existsByOrder_OrderId(orderDbId)) {
                    PayOrderModel paidOrder = new PayOrderModel();
                    paidOrder.setOrder(order);
                    paidOrder.setUserId(order.getUserId());
                    paidOrder.setCustomerName(order.getCustomerName());
                    paidOrder.setCustomerEmail(order.getCustomerEmail());
                    paidOrder.setStatus("COMPLETED");
                    paidOrder.setTotalAmount(order.getTotalAmount());
                    paidOrder.setItems(order.getItems());
                    paidOrder.setOrderDate(order.getOrderDate());
                    paidOrderRepository.save(paidOrder);
                }
            }
        }

        return status;
    }
    // ===============================
    // BUILD PAYPAL REQUEST
    // ===============================
    private OrderRequest buildRequestBody(Double amount, Long orderDbId) {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // Route back to backend callback when we know order context, otherwise keep frontend success page.
        String returnUrl = orderDbId != null
                ? "http://localhost:8082/api/payments/paypal/success?amount=" + amount + "&orderDbId=" + orderDbId
                : "http://localhost:5173/success";

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl("http://localhost:5173/cancel");

        PurchaseUnitRequest purchaseUnitRequest =
                new PurchaseUnitRequest()
                        .amountWithBreakdown(
                                new AmountWithBreakdown()
                                        .currencyCode("USD")
                                        .value(String.valueOf(amount))
                        );

        orderRequest.applicationContext(applicationContext);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        return orderRequest;
    }
}