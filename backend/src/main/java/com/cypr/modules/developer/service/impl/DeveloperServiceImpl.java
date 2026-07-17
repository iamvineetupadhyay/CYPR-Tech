package com.cypr.modules.developer.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.developer.dto.WebhookEndpointRequestDTO;
import com.cypr.modules.developer.dto.WebhookEndpointResponseDTO;
import com.cypr.modules.developer.dto.WebhookEventLogDTO;
import com.cypr.modules.developer.entity.WebhookEndpoint;
import com.cypr.modules.developer.entity.WebhookEventLog;
import com.cypr.modules.developer.repository.WebhookEndpointRepository;
import com.cypr.modules.developer.repository.WebhookEventLogRepository;
import com.cypr.modules.developer.service.DeveloperService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeveloperServiceImpl implements DeveloperService {

    private final WebhookEndpointRepository webhookEndpointRepository;
    private final WebhookEventLogRepository webhookEventLogRepository;
    private final UserRepository userRepository;

    public DeveloperServiceImpl(WebhookEndpointRepository webhookEndpointRepository, WebhookEventLogRepository webhookEventLogRepository, UserRepository userRepository) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.webhookEventLogRepository = webhookEventLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WebhookEndpointResponseDTO registerWebhook(WebhookEndpointRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
                
        WebhookEndpoint endpoint = new WebhookEndpoint();
        endpoint.setUser(user);
        endpoint.setUrl(request.getUrl());
        endpoint.setSecret(request.getSecret());
        endpoint.setEvents(request.getEvents());
        endpoint.setActive(request.isActive());
        
        endpoint = webhookEndpointRepository.save(endpoint);
        return mapToDTO(endpoint);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WebhookEndpointResponseDTO> getUserWebhooks(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        return webhookEndpointRepository.findByUser(user, pageable).map(this::mapToDTO);
    }

    @Override
    public WebhookEndpointResponseDTO updateWebhookStatus(UUID id, boolean isActive) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Webhook not found"));
        endpoint.setActive(isActive);
        endpoint = webhookEndpointRepository.save(endpoint);
        return mapToDTO(endpoint);
    }

    @Override
    public void deleteWebhook(UUID id) {
        webhookEndpointRepository.deleteById(id);
    }

    @Override
    public void triggerMockWebhook(UUID endpointId, String eventType, String payload) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(endpointId)
                .orElseThrow(() -> new BusinessException("Webhook not found"));
                
        WebhookEventLog log = new WebhookEventLog();
        log.setEndpoint(endpoint);
        log.setEventType(eventType);
        log.setPayload(payload);
        
        // Mock successful dispatch
        log.setStatus("SUCCESS");
        log.setResponseCode(200);
        
        webhookEventLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WebhookEventLogDTO> getWebhookLogs(UUID endpointId, Pageable pageable) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(endpointId)
                .orElseThrow(() -> new BusinessException("Webhook not found"));
        return webhookEventLogRepository.findByEndpoint(endpoint, pageable).map(l -> {
            WebhookEventLogDTO dto = new WebhookEventLogDTO();
            dto.setId(l.getId());
            dto.setEndpointId(l.getEndpoint().getId());
            dto.setEventType(l.getEventType());
            dto.setPayload(l.getPayload());
            dto.setResponseCode(l.getResponseCode());
            dto.setStatus(l.getStatus());
            return dto;
        });
    }

    private WebhookEndpointResponseDTO mapToDTO(WebhookEndpoint endpoint) {
        WebhookEndpointResponseDTO dto = new WebhookEndpointResponseDTO();
        dto.setId(endpoint.getId());
        dto.setUserId(endpoint.getUser().getId());
        dto.setUrl(endpoint.getUrl());
        dto.setEvents(endpoint.getEvents());
        dto.setActive(endpoint.isActive());
        return dto;
    }
}
