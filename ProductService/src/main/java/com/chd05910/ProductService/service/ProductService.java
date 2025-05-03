package com.chd05910.ProductService.service;

import com.chd05910.ProductService.model.ProductRequest;
import com.chd05910.ProductService.model.ProductResponse;

public interface ProductService {
    public long addProduct(ProductRequest productRequest);

    public ProductResponse getProductById(Long id);

    public void reduceQuantity(long productId, long quantity);
}
