package com.cypr.modules.communication.repository;

import com.cypr.modules.communication.entity.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailCampaignRepository extends JpaRepository<EmailCampaign, UUID>, JpaSpecificationExecutor<EmailCampaign> {
}
