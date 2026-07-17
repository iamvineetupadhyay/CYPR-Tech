package com.cypr.modules.security.repository;

import com.cypr.modules.security.entity.BlacklistedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlacklistedIpRepository extends JpaRepository<BlacklistedIp, UUID>, JpaSpecificationExecutor<BlacklistedIp> {
    Optional<BlacklistedIp> findByIpAddress(String ipAddress);
    boolean existsByIpAddress(String ipAddress);
}
