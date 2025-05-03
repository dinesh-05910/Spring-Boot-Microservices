package com.chd05910.OrderService.service;
import com.chd05910.OrderService.entity.OrderEntity;
import com.chd05910.OrderService.exception.CustomException;
import com.chd05910.OrderService.external.client.PaymentServiceClient;
import com.chd05910.OrderService.external.client.ProductServiceClient;
import com.chd05910.OrderService.external.model.PaymentResponse;
import com.chd05910.OrderService.external.model.ProductResponse;
import com.chd05910.OrderService.external.request.PaymentRequest;
import com.chd05910.OrderService.model.OrderRequest;
import com.chd05910.OrderService.model.OrderResponse;
import com.chd05910.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private RestTemplate restTemplate;

    public long placeOrder(OrderRequest orderRequest) {
        productServiceClient.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Product Quantity Successfully Reduced by: {}", orderRequest.getQuantity());
        log.info("Placing Order Request : {} ", orderRequest);
        OrderEntity orderEntity = OrderEntity.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .totalAmount(orderRequest.getTotalAmount())
                .build();
        orderEntity = orderRepository.save(orderEntity);
        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(orderEntity.getId())
                .amount(orderRequest.getTotalAmount())
                .paymentMode(orderRequest.getPaymentMode())
                .build();
        String orderStatus = null;
        try {
            paymentServiceClient.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the order status to Paid");
            orderStatus = "PLACED";
        }
        catch(Exception e) {
            log.error("Error occurred in the payment. Changing the order status to Payment Failed");
            orderStatus = "PAYMENT_FAILED";
        }
        orderEntity.setOrderStatus(orderStatus);
        orderRepository.save(orderEntity);
        log.info("Order Placed Successfully with Order Id : {}", orderEntity.getId());
        return orderEntity.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long id) {
       OrderEntity orderEntity = orderRepository.findById(id)
               .orElseThrow(() -> new CustomException("Order Not Found with given id","ORDER_NOT_FOUND",404));
       log.info("Invoking Product Service to get the Product Details");
       ProductResponse productResponse = restTemplate.getForObject(
               "http://ProductService/products/" + orderEntity.getProductId(),
               ProductResponse.class
       );
       OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
               .productId(productResponse.getProductId())
               .productName(productResponse.getProductName())
               .price(productResponse.getPrice())
               .build();
       log.info("Invoking Payment Service to get the Payment Details");
       PaymentResponse paymentResponse = restTemplate.getForObject(
               "http://PaymentService/payment/" + id,
               PaymentResponse.class
       );
       OrderResponse.PaymentDetails paymentDetails= OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentMode(paymentResponse.getPaymentMode())
                .referenceNumber(paymentResponse.getReferenceNumber())
                .paymentStatus(paymentResponse.getPaymentStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .build();
       OrderResponse orderResponse = OrderResponse.builder()
               .orderId(orderEntity.getId())
               .orderStatus(orderEntity.getOrderStatus())
               .orderDate(orderEntity.getOrderDate())
               .amount(orderEntity.getTotalAmount())
               .productDetails(productDetails)
               .paymentDetails(paymentDetails)
               .build();
       return orderResponse;
    }
}
