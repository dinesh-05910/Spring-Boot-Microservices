package com.chd05910.PaymentService.controller;

import com.chd05910.PaymentService.model.PaymentRequest;
import com.chd05910.PaymentService.model.PaymentResponse;
import com.chd05910.PaymentService.service.PaymentService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable Long orderId) {
        return new ResponseEntity<>(paymentService.getPaymentDetails(orderId), HttpStatus.OK);
    }
}
