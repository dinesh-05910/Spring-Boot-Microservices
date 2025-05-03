package com.chd05910.OrderService.controller;

import com.chd05910.OrderService.model.OrderRequest;
import com.chd05910.OrderService.model.OrderResponse;
import com.chd05910.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping("/getOrderDetails/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long id) {
        return new ResponseEntity<>(orderService.getOrderDetails(id),HttpStatus.OK);
    }
}
