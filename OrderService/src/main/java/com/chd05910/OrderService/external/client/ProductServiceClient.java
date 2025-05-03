package com.chd05910.OrderService.external.client;

import com.chd05910.OrderService.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CircuitBreaker(name = "external", fallbackMethod = "fallBack")
@FeignClient(name = "ProductService/products")
public interface ProductServiceClient {
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam long quantity);

    default void fallBack(Exception e) {
        throw new CustomException("Product Service Not Available!", "SERVICE_UNAVAILABLE",500);
    }
}
