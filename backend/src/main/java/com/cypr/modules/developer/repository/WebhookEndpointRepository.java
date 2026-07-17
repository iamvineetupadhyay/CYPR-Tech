package com.cypr.modules.developer.repository;

import com.cypr.entity.User;
import com.cypr.modules.developer.entity.WebhookEndpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, UUID>, JpaSpecificationExecutor<WebhookEndpoint> {
    Page<WebhookEndpoint> findByUser(User user, Pageable pageable);
}
