package com.cypr.modules.communication.repository;

import com.cypr.modules.communication.entity.EmailCampaign;
import com.cypr.modules.communication.entity.EmailDeliveryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailDeliveryLogRepository extends JpaRepository<EmailDeliveryLog, UUID>, JpaSpecificationExecutor<EmailDeliveryLog> {
    Page<EmailDeliveryLog> findByEmailCampaign(EmailCampaign campaign, Pageable pageable);
}
