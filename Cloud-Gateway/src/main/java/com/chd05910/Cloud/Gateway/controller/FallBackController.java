package com.chd05910.Cloud.Gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBack() {
        return "Order Service is Down. Please try after sometime!";
    }

    @GetMapping("/productServiceFallBack")
    public String productServiceFallBack() {
        return "Product Service is Down. Please try after sometime!";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack() {
        return "Payment Service is Down. Please try after sometime!";
    }
}
