package com.example.OrderService.workers;

import com.example.OrderService.service.BillQueueService;
import com.example.OrderService.service.BillService;
import com.example.OrderService.dto.OrderDto;
import org.springframework.stereotype.Component;

@Component
public class BillPrinterWorker {

    private final BillQueueService billQueueService;
    private final BillService billService;

    public BillPrinterWorker(BillQueueService billQueueService, BillService billService) {
        this.billQueueService = billQueueService;
        this.billService = billService;

        new Thread(this::processCustomerBills).start();
        new Thread(this::processDriverBills).start();
    }

    private void processCustomerBills() {
        while (true) {
            try {
                OrderDto order = billQueueService.getNextCustomerBill();
                System.out.println(billService.generateCustomerBill(order));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processDriverBills() {
        while (true) {
            try {
                OrderDto order = billQueueService.getNextDriverBill();
                System.out.println(billService.generateDriverBill(order));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
