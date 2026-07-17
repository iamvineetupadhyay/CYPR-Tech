package com.cypr.modules.billing.repository;

import com.cypr.modules.billing.entity.Payment;
import com.cypr.modules.billing.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID>, JpaSpecificationExecutor<Refund> {
    Page<Refund> findByPayment(Payment payment, Pageable pageable);
}
