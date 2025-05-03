package com.chd05910.OrderService.controller;

import com.chd05910.OrderService.MockRestTemplateConfig;
import com.chd05910.OrderService.OrderServiceConfig;
import com.chd05910.OrderService.entity.OrderEntity;
import com.chd05910.OrderService.external.model.PaymentResponse;
import com.chd05910.OrderService.external.model.ProductResponse;
import com.chd05910.OrderService.model.OrderRequest;
import com.chd05910.OrderService.model.PaymentMode;
import com.chd05910.OrderService.repository.OrderRepository;
import com.chd05910.OrderService.service.OrderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"})
@AutoConfigureMockMvc
@EnableConfigurationProperties
@ContextConfiguration(classes = {OrderServiceConfig.class})
@Import(MockRestTemplateConfig.class)
@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Autowired
    @Qualifier("mockRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8989))
            .build();

    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void reduceQuantity() {
        wireMockServer.stubFor(put(urlMatching("/products/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                ));
    }

    private void getPaymentDetails() throws IOException {
        wireMockServer.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                OrderControllerTest.class
                                        .getClassLoader()
                                        .getResourceAsStream("/mock/GetPayment.json"),
                                defaultCharset()
                        ))
                ));
    }

    private void doPayment() {
        wireMockServer.stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        wireMockServer.stubFor(get(urlMatching("/products/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                OrderControllerTest.class
                                        .getClassLoader()
                                        .getResourceAsStream("/mock/GetProduct.json"),
                                defaultCharset()
                        ))
                ));
    }


    @DisplayName("Integration Testing - Place Order - Success Scenario!")
    @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        //First Place Order
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(orderRequest)
                )).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();
        System.out.println(orderId);

        //Get Order By Order ID from DB and check
        Optional<OrderEntity> order = orderRepository.findById(Long.valueOf(orderId));

        //Check output
        assertTrue(order.isPresent());

        OrderEntity orderEntity = order.get();
        assertEquals(Long.parseLong(orderId), orderEntity.getId());
        assertEquals("PLACED", orderEntity.getOrderStatus());
        assertEquals(orderRequest.getQuantity(), orderEntity.getQuantity());
        assertEquals(orderRequest.getTotalAmount(), orderEntity.getTotalAmount());
    }


    @DisplayName("Integration Testing - Get Order Details - Success Scenario!")
    @Test
    public void test_WhenGetOrder_Success() throws Exception {
        ProductResponse mockProduct = new ProductResponse(1L, "Samsung", 100, 10);
        PaymentResponse mockPayment = new PaymentResponse(1,1,"CASH","shssk", Instant.now(),"SUCCESS",2000);
        Mockito.when(restTemplate.getForObject("http://ProductService/products/1", ProductResponse.class)).thenReturn(mockProduct);
        Mockito.when(restTemplate.getForObject("http://PaymentService/payment/1852", PaymentResponse.class))
                .thenReturn(mockPayment);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/order/getOrderDetails/1852")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualResponse);
//        OrderEntity orderEntity = orderRepository.findById(1852L).get();
//        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
//                .productId(mockProduct.getProductId())
//                .productName(mockProduct.getProductName())
//                .price(mockProduct.getPrice())
//                .build();
//
//        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
//                .paymentId(mockPayment.getPaymentId())
//                .paymentMode(mockPayment.getPaymentMode())
//                .referenceNumber(mockPayment.getReferenceNumber())
//                .paymentStatus("SUCCESS")
//                .paymentDate(mockPayment.getPaymentDate())
//                .build();
//
//        OrderResponse expectedOrderResponse = OrderResponse.builder()
//                .orderId(orderEntity.getId())
//                .orderStatus(orderEntity.getOrderStatus())
//                .orderDate(orderEntity.getOrderDate())
//                .amount(orderEntity.getTotalAmount())
//                .productDetails(productDetails)
//                .paymentDetails(paymentDetails)
//                .build();
//
//        String expectedResponse = objectMapper.writeValueAsString(expectedOrderResponse);
//
//        // Assert
//        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("Integration Testing - Order Not Found!")
    @Test
    public void test_WhenGetOrder_Order_Not_Found() throws Exception {
        ProductResponse mockProduct = new ProductResponse(1L, "Samsung", 100, 10);
        PaymentResponse mockPayment = new PaymentResponse(1,1,"CASH","shssk", Instant.now(),"SUCCESS",2000);
        Mockito.when(restTemplate.getForObject("http://ProductService/products/1", ProductResponse.class)).thenReturn(mockProduct);
        Mockito.when(restTemplate.getForObject("http://PaymentService/payment/1852", PaymentResponse.class))
                .thenReturn(mockPayment);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/order/getOrderDetails/2000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }



    private OrderRequest getMockOrderRequest() {
        OrderRequest orderRequest = OrderRequest.builder()
                .totalAmount(2000)
                .quantity(2)
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .build();
        return orderRequest;
    }
}