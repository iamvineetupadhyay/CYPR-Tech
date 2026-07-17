package com.cypr.modules.security.repository;

import com.cypr.modules.security.entity.AdminAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AdminAuditLogRepository extends Repository<AdminAuditLog, Long> {
    AdminAuditLog save(AdminAuditLog entity);
    Optional<AdminAuditLog> findById(Long id);
    Page<AdminAuditLog> findAll(Pageable pageable);
    Page<AdminAuditLog> findByAction(String action, Pageable pageable);
}
