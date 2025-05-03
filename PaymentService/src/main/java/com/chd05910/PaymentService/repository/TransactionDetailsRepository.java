package com.chd05910.PaymentService.repository;

import com.chd05910.PaymentService.entity.TransactionDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetailsEntity, Long> {

    TransactionDetailsEntity findByOrderId(long orderId);
}
