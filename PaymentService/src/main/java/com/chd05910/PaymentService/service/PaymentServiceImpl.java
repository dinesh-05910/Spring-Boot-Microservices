package com.chd05910.PaymentService.service;

import com.chd05910.PaymentService.entity.TransactionDetailsEntity;
import com.chd05910.PaymentService.model.PaymentRequest;
import com.chd05910.PaymentService.model.PaymentResponse;
import com.chd05910.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details and Performing Payment:");
        TransactionDetailsEntity transactionDetailsEntity = TransactionDetailsEntity.builder()
                .orderId(paymentRequest.getOrderId())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .referenceNumber(UUID.randomUUID().toString())
                .paymentDate(Instant.now())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .build();
        transactionDetailsRepository.save(transactionDetailsEntity);
        log.info("Payment is successful!. Here is your Payment Id : {}", transactionDetailsEntity.getId());
        return transactionDetailsEntity.getId();
    }

    @Override
    public PaymentResponse getPaymentDetails(Long orderId) {
        log.info("Retrieving the payment Details for the orderId: {}", orderId );
        TransactionDetailsEntity transactionDetailsEntity = transactionDetailsRepository.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetailsEntity.getId())
                .orderId(transactionDetailsEntity.getOrderId())
                .paymentMode(transactionDetailsEntity.getPaymentMode())
                .referenceNumber(transactionDetailsEntity.getReferenceNumber())
                .paymentDate(transactionDetailsEntity.getPaymentDate())
                .paymentStatus(transactionDetailsEntity.getPaymentStatus())
                .amount(transactionDetailsEntity.getAmount())
                .build();
        return paymentResponse;
    }
}
