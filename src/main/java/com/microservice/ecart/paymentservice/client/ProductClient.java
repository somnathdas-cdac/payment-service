package com.microservice.ecart.paymentservice.client;


import com.microservice.ecart.paymentservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    // Make sure this method name matches the one called in your service
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id); 
}