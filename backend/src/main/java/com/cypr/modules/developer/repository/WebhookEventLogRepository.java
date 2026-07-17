package com.cypr.modules.developer.repository;

import com.cypr.modules.developer.entity.WebhookEndpoint;
import com.cypr.modules.developer.entity.WebhookEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookEventLogRepository extends JpaRepository<WebhookEventLog, UUID>, JpaSpecificationExecutor<WebhookEventLog> {
    Page<WebhookEventLog> findByEndpoint(WebhookEndpoint endpoint, Pageable pageable);
}
