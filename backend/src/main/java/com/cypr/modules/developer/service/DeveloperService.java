package com.cypr.modules.developer.service;

import com.cypr.modules.developer.dto.WebhookEndpointRequestDTO;
import com.cypr.modules.developer.dto.WebhookEndpointResponseDTO;
import com.cypr.modules.developer.dto.WebhookEventLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface DeveloperService {
    WebhookEndpointResponseDTO registerWebhook(WebhookEndpointRequestDTO request);
    Page<WebhookEndpointResponseDTO> getUserWebhooks(Long userId, Pageable pageable);
    WebhookEndpointResponseDTO updateWebhookStatus(UUID id, boolean isActive);
    void deleteWebhook(UUID id);
    
    // Testing logic
    void triggerMockWebhook(UUID endpointId, String eventType, String payload);
    Page<WebhookEventLogDTO> getWebhookLogs(UUID endpointId, Pageable pageable);
}
