package com.cypr.modules.billing.repository;

import com.cypr.entity.User;
import com.cypr.modules.billing.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    @EntityGraph(attributePaths = {"user"})
    Page<Payment> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Payment> findByTransactionId(String transactionId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' OR p.status = 'PAID'")
    java.math.BigDecimal getTotalRevenue();

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM payments p WHERE " +
           "to_tsvector('english', coalesce(p.transaction_id, '') || ' ' || coalesce(p.provider, '') || ' ' || coalesce(p.status, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(p.transaction_id, '') || ' ' || coalesce(p.provider, '') || ' ' || coalesce(p.status, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM payments p WHERE " +
           "to_tsvector('english', coalesce(p.transaction_id, '') || ' ' || coalesce(p.provider, '') || ' ' || coalesce(p.status, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    Page<Payment> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, Pageable pageable);
}
