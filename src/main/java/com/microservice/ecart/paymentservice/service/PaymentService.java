package com.microservice.ecart.paymentservice.service;



import com.microservice.ecart.paymentservice.client.OrderClient;
import com.microservice.ecart.paymentservice.client.ProductClient;
import com.microservice.ecart.paymentservice.dto.ProductDTO;
import com.microservice.ecart.paymentservice.model.Payment;
import com.microservice.ecart.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    // Constructor Injection for Repository and Feign Clients
    public PaymentService(PaymentRepository paymentRepository, 
                          ProductClient productClient, 
                          OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.productClient = productClient;
        this.orderClient = orderClient;
    }

    /**
     * Processes a demo payment with random success/failure outcomes.
     * Verifies product data from Product Service and updates Order Service status.
     */
    
    
    public Payment processPayment(Long orderId, Long productId, BigDecimal amount) {
        // 1. Fetch data from Product Service via Feign (Validation step)
        try {
            ProductDTO product = productClient.getProductById(productId);
            System.out.println("Processing transaction for item: " + product.getName());
        } catch (Exception e) {
            System.err.println("Warning: Product-Service unreachable. Moving ahead with demo transaction.");
        }
        
        

        // 2. Generate a random status (70% SUCCESS, 30% FAILED)
        String status = new Random().nextInt(10) < 7 ? "SUCCESS" : "FAILED";

        // 3. Save the payment record locally in H2
        Payment payment = new Payment(orderId, productId, amount, status);
        Payment savedPayment = paymentRepository.save(payment);

        // 4. Update the Order Service with the transaction status via Feign
        try {
            orderClient.updateOrderStatus(orderId, status);
            System.out.println("Dispatched status update [" + status + "] to Order Service.");
        } catch (Exception e) {
            System.err.println("Error: Failed to synchronize status with Order Service: " + e.getMessage());
        }

        return savedPayment;
    }

    /**
     * Processes refunds for successful transactions.
     */
    public Payment refundPayment(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            if ("SUCCESS".equals(payment.getStatus())) {
                payment.setStatus("REFUNDED");
                Payment updatedPayment = paymentRepository.save(payment);
                
                // Optional: Notify Order Service about the refund status
                try {
                    orderClient.updateOrderStatus(payment.getOrderId(), "REFUNDED");
                } catch (Exception e) {
                    System.err.println("Failed to sync refund with Order Service.");
                }
                
                return updatedPayment;
            }
        }
        throw new RuntimeException("Transaction not eligible for refund processing.");
    }

    /**
     * Generates a plain-text invoice file content.
     */
    public String generateInvoice(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Requested transaction record not found."));

        return "=================================\n" +
               "         PAYMENT INVOICE         \n" +
               "=================================\n" +
               "Transaction ID : " + payment.getId() + "\n" +
               "Order Reference: " + payment.getOrderId() + "\n" +
               "Product Reference: " + payment.getProductId() + "\n" +
               "Total Amount   : $" + payment.getAmount() + "\n" +
               "Current Status : " + payment.getStatus() + "\n" +
               "=================================";
    }
}
