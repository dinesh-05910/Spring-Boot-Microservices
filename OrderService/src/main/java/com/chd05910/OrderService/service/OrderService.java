package com.chd05910.OrderService.service;

import com.chd05910.OrderService.model.OrderRequest;
import com.chd05910.OrderService.model.OrderResponse;

public interface OrderService {
    public long placeOrder(OrderRequest orderRequest);

    public OrderResponse getOrderDetails(long id);
}
