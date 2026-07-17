package com.cypr.modules.security.repository;

import com.cypr.entity.User;
import com.cypr.modules.security.entity.FraudDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FraudDetectionRepository extends JpaRepository<FraudDetection, UUID>, JpaSpecificationExecutor<FraudDetection> {
    List<FraudDetection> findByUser(User user);
    List<FraudDetection> findByUserAndStatus(User user, String status);
}
