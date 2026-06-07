package com.microservice.ecart.paymentservice.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ORDER-SERVICE must match the application name registered in your Eureka Server dashboard
@FeignClient(name = "ORDER-SERVICE") 
public interface OrderClient {

    /**
     * Sends a POST request to the Order Service to update the status of an order.
     * Maps to: POST http://ORDER-SERVICE/api/orders/{id}/update-status?status=SUCCESS
     *
     * @param id the ID of the order to update
     * @param status the new payment status (e.g., SUCCESS, FAILED, REFUNDED)
     * @return a confirmation string or response from the Order Service
     */
    @PostMapping("/api/orders/{id}/update-status")
    String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}
