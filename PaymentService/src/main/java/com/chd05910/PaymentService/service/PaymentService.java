package com.chd05910.PaymentService.service;

import com.chd05910.PaymentService.model.PaymentRequest;
import com.chd05910.PaymentService.model.PaymentResponse;

public interface PaymentService {
    public Long doPayment(PaymentRequest paymentRequest);

    public PaymentResponse getPaymentDetails(Long orderId);
}
