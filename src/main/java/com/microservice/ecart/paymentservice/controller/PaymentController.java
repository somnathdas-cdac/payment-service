package com.microservice.ecart.paymentservice.controller;

import com.microservice.ecart.paymentservice.dto.PaymentRequest; // Added import for the DTO

import com.microservice.ecart.paymentservice.model.Payment;
import com.microservice.ecart.paymentservice.service.PaymentService;

import com.microservice.ecart.paymentservice.dto.ProductDTO; 



import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

 
    
    
    @PostMapping("/process")
    public ResponseEntity<Payment> process(@RequestBody PaymentRequest paymentRequest) {
        // FIXED: Replaced fragile manual map parsing with clean, type-safe getters
        Payment payment = paymentService.processPayment(
            paymentRequest.getOrderId(), 
            paymentRequest.getProductId(), 
            paymentRequest.getAmount()
        );
        return ResponseEntity.ok(payment);
    }
    
    
    /*
    @PostMapping("/process")
    public ResponseEntity<Payment> process( PaymentRequest paymentRequest) {
        // FIXED: Replaced fragile manual map parsing with clean, type-safe getters
        Payment payment = paymentService.processPayment(
            paymentRequest.getOrderId(), 
            paymentRequest.getProductId(), 
            paymentRequest.getAmount()
        );
        return ResponseEntity.ok(payment);
    }
    */
   
    
    
    

    @PostMapping("/refund/{id}")
    public ResponseEntity<Payment> refund(@PathVariable Long id) {
        Payment refundedPayment = paymentService.refundPayment(id);
        return ResponseEntity.ok(refundedPayment);
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        String invoiceContent = paymentService.generateInvoice(id);
        byte[] data = invoiceContent.getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(data.length)
                .body(data);
    }
}
