package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.BulkEmailRequestDTO;
import com.cypr.modules.communication.dto.EmailCampaignResponseDTO;
import com.cypr.modules.communication.dto.EmailDeliveryLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface EmailService {
    EmailCampaignResponseDTO scheduleBulkEmail(BulkEmailRequestDTO request);
    Page<EmailCampaignResponseDTO> getCampaignHistory(Pageable pageable);
    Page<EmailDeliveryLogDTO> getCampaignDeliveryLogs(UUID campaignId, Pageable pageable);
    void processScheduledEmails(); // For cron jobs
}
