package com.chd05910.OrderService.external.client;

import com.chd05910.OrderService.exception.CustomException;
import com.chd05910.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@CircuitBreaker(name = "external", fallbackMethod = "fallBack")
@FeignClient(name = "PaymentService/payment")
public interface PaymentServiceClient {
    @PostMapping
    public Long doPayment(PaymentRequest paymentRequest);

    default void fallBack(Exception e) {
        throw new CustomException("Payment Service Not Available", "SERVICE_UNAVAILABLE", 500);
    }
}
