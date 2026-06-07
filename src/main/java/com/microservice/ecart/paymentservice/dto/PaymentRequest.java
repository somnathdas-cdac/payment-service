package com.microservice.ecart.paymentservice.dto;



import java.math.BigDecimal;

public class PaymentRequest {
    private Long orderId;
    private Long productId;
    private BigDecimal amount;

    // Default Constructor required by Jackson for JSON deserialization
    public PaymentRequest() {}

    public PaymentRequest(Long orderId, Long productId, BigDecimal amount) {
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
