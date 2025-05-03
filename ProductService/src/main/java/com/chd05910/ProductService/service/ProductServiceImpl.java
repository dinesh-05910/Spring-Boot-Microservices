package com.chd05910.ProductService.service;

import com.chd05910.ProductService.entity.ProductEntity;
import com.chd05910.ProductService.exception.ProductServiceCustomException;
import com.chd05910.ProductService.model.ProductRequest;
import com.chd05910.ProductService.model.ProductResponse;
import com.chd05910.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product...");
        ProductEntity productEntity = ProductEntity.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(productEntity);
        log.info("Product is created and saved in DB");
        return productEntity.getProductId();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Getting Product Details...");
        ProductEntity productEntity = productRepository
                .findById(id)
                .orElseThrow(() -> new ProductServiceCustomException("No Product Found with id: " + id, "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(productEntity,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id {}", quantity, productId);
        ProductEntity productEntity = productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product Not Found", "PRODUCT_NOT_FOUND"));
        if ( productEntity.getQuantity() < quantity ) {
            throw new ProductServiceCustomException("No Sufficient Quantity Available", "INSUFFICIENT_QUANTITY");
        }
        productEntity.setQuantity(productEntity.getQuantity() - quantity);
        productRepository.save(productEntity);
        log.info("Product Quantity has been updated Successfully!");
    }
}
