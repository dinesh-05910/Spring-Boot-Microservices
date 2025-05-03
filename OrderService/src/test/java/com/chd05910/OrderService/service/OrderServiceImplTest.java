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
import com.chd05910.OrderService.model.PaymentMode;
import com.chd05910.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order Details- Success Scenario!")
    @Test
    void test_When_Order_Success() {

        //Mocking
        OrderEntity orderEntity = getMockOrderEntity();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(orderEntity));

        ProductResponse productResponse = getMockProductResponse();
        when(restTemplate.getForObject(
                "http://ProductService/products/" + orderEntity.getProductId(),
                ProductResponse.class
        )).thenReturn(productResponse);

        PaymentResponse paymentResponse = getMockPaymentResponse();
        when(restTemplate.getForObject(
                "http://PaymentService/payment/" + orderEntity.getId(),
                PaymentResponse.class
        )).thenReturn(paymentResponse);

        //Actual Method call
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        //Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://ProductService/products/" + orderEntity.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate,times(1)).getForObject(
                "http://PaymentService/payment/" + orderEntity.getId(),
                PaymentResponse.class
        );

        //Assertion
        assertNotNull(orderResponse);
        assertEquals(orderEntity.getId(), orderResponse.getOrderId());

    }

    @DisplayName("Get Order Details - Failure Scenario!")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        //Mock
        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        //Actual Call
        CustomException exception = assertThrows(CustomException.class, () -> orderService.getOrderDetails(1));

        //Verification
        verify(orderRepository,times(1)).findById(anyLong());

        //Assertion
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
    }

    @DisplayName("Place Order - Success Scenario!")
    @Test
    void test_When_Place_Order_Success() {
        OrderEntity orderEntity = getMockOrderEntity();
        OrderRequest orderRequest = getMockOrderRequest();

        //Mock
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(productServiceClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentServiceClient.doPayment(any(PaymentRequest.class)))
                .thenReturn(1L);

        //Actual Call
        long orderId = orderService.placeOrder(orderRequest);

        //Verification
        verify(orderRepository, times(2)).save(any());
        verify(productServiceClient, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentServiceClient, times(1)).doPayment(any(PaymentRequest.class));

        //Assertion
        assertEquals(orderEntity.getId(), orderId);

    }

    @DisplayName("Place Order - Payment Failed Scenario!")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed() {
        OrderEntity orderEntity = getMockOrderEntity();
        OrderRequest orderRequest = getMockOrderRequest();

        //Mock
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(productServiceClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentServiceClient.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        //Actual Call
        long orderId = orderService.placeOrder(orderRequest);

        //Verification
        verify(orderRepository, times(2)).save(any());
        verify(productServiceClient, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentServiceClient, times(1)).doPayment(any(PaymentRequest.class));

        //Assertion
        assertEquals(orderEntity.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        OrderRequest orderRequest = OrderRequest.builder()
                .paymentMode(PaymentMode.CASH)
                .productId(1)
                .quantity(10)
                .totalAmount(1000)
                .build();
        return orderRequest;
    }


    private PaymentResponse getMockPaymentResponse() {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .orderId(1)
                .paymentId(8379)
                .paymentDate(Instant.now())
                .paymentMode("CASH")
                .paymentStatus("SUCCESS")
                .amount(2000)
                .referenceNumber("bshgs-shshj")
                .build();
        return paymentResponse;
    }

    private OrderEntity getMockOrderEntity() {
        OrderEntity orderEntity = OrderEntity.builder()
                .orderStatus("SUCCESS")
                .id(1)
                .orderDate(Instant.now())
                .productId(10)
                .quantity(10)
                .totalAmount(2000)
                .build();
        return orderEntity;
    }

    private ProductResponse getMockProductResponse() {
        ProductResponse productResponse = ProductResponse.builder()
                .productName("iPhone XR")
                .price(599)
                .productId(2)
                .quantity(15)
                .build();
        return productResponse;
    }

}