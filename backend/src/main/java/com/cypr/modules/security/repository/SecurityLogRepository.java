package com.cypr.modules.security.repository;

import com.cypr.entity.User;
import com.cypr.modules.security.entity.SecurityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, UUID>, JpaSpecificationExecutor<SecurityLog> {

    @EntityGraph(attributePaths = {"user"})
    Page<SecurityLog> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<SecurityLog> findBySeverity(String severity, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM SecurityLog s WHERE s.severity = 'HIGH' OR s.severity = 'CRITICAL'")
    long countHighSeverityThreats();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(s) FROM SecurityLog s WHERE s.event = 'FAILED_LOGIN'")
    long countFailedLogins();
}
