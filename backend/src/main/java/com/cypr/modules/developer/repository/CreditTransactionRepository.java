package com.cypr.modules.developer.repository;

import com.cypr.modules.developer.entity.CreditTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CreditTransactionRepository extends Repository<CreditTransaction, Long> {
    CreditTransaction save(CreditTransaction entity);
    Optional<CreditTransaction> findById(Long id);
    Page<CreditTransaction> findAll(Pageable pageable);
    Page<CreditTransaction> findByTargetUserId(Long targetUserId, Pageable pageable);
}
