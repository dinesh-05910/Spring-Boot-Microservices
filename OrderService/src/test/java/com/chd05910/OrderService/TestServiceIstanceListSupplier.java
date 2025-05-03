package com.chd05910.OrderService;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class TestServiceIstanceListSupplier implements ServiceInstanceListSupplier {

    @Override
    public String getServiceId() {
        return "";
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> result = new ArrayList<>();
        result.add(new DefaultServiceInstance(
                "PaymentService",
                "PaymentService",
                "localhost",
                8989,
                false
        ));
        result.add(new DefaultServiceInstance(
                "ProductService",
                "ProductService",
                "localhost",
                8989,
                false
        ));
        return Flux.just(result);
    }
}
