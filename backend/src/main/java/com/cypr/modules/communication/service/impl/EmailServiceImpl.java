package com.cypr.modules.communication.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.BulkEmailRequestDTO;
import com.cypr.modules.communication.dto.EmailCampaignResponseDTO;
import com.cypr.modules.communication.dto.EmailDeliveryLogDTO;
import com.cypr.modules.communication.entity.EmailCampaign;
import com.cypr.modules.communication.entity.EmailDeliveryLog;
import com.cypr.modules.communication.entity.EmailTemplate;
import com.cypr.modules.communication.repository.EmailCampaignRepository;
import com.cypr.modules.communication.repository.EmailDeliveryLogRepository;
import com.cypr.modules.communication.repository.EmailTemplateRepository;
import com.cypr.modules.communication.service.EmailService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final EmailCampaignRepository emailCampaignRepository;
    private final EmailDeliveryLogRepository emailDeliveryLogRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final UserRepository userRepository;

    public EmailServiceImpl(
            EmailCampaignRepository emailCampaignRepository,
            EmailDeliveryLogRepository emailDeliveryLogRepository,
            EmailTemplateRepository emailTemplateRepository,
            UserRepository userRepository) {
        this.emailCampaignRepository = emailCampaignRepository;
        this.emailDeliveryLogRepository = emailDeliveryLogRepository;
        this.emailTemplateRepository = emailTemplateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EmailCampaignResponseDTO scheduleBulkEmail(BulkEmailRequestDTO request) {
        EmailCampaign campaign = new EmailCampaign();
        
        if (request.getTemplateId() != null) {
            EmailTemplate template = emailTemplateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new BusinessException("Template not found"));
            campaign.setSubject(template.getSubject());
            campaign.setBodyHtml(template.getBodyHtml());
        } else {
            campaign.setSubject(request.getSubject());
            campaign.setBodyHtml(request.getBodyHtml());
        }

        campaign.setStatus(request.getScheduledAt() == null || request.getScheduledAt().isBefore(LocalDateTime.now()) ? "SENDING" : "PENDING");
        campaign.setScheduledAt(request.getScheduledAt());

        List<User> targetUsers;
        if (request.getRecipientUserIds() != null && !request.getRecipientUserIds().isEmpty()) {
            targetUsers = userRepository.findAllById(request.getRecipientUserIds());
        } else if (request.getSubscriptionTypeFilter() != null && !request.getSubscriptionTypeFilter().isEmpty()) {
            targetUsers = userRepository.findAll().stream()
                    .filter(u -> request.getSubscriptionTypeFilter().equals(u.getSubscriptionType()))
                    .toList();
        } else {
            targetUsers = userRepository.findAll();
        }

        campaign.setTotalRecipients(targetUsers.size());
        campaign = emailCampaignRepository.save(campaign);

        // Prepare delivery logs
        for (User user : targetUsers) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                EmailDeliveryLog log = new EmailDeliveryLog();
                log.setEmailCampaign(campaign);
                log.setRecipientEmail(user.getEmail());
                log.setStatus("PENDING");
                emailDeliveryLogRepository.save(log);
            }
        }

        EmailCampaignResponseDTO response = new EmailCampaignResponseDTO();
        response.setId(campaign.getId());
        response.setStatus(campaign.getStatus());
        response.setTotalRecipients(campaign.getTotalRecipients());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailCampaignResponseDTO> getCampaignHistory(Pageable pageable) {
        return emailCampaignRepository.findAll(pageable).map(c -> {
            EmailCampaignResponseDTO dto = new EmailCampaignResponseDTO();
            dto.setId(c.getId());
            dto.setSubject(c.getSubject());
            dto.setStatus(c.getStatus());
            dto.setScheduledAt(c.getScheduledAt());
            dto.setSentAt(c.getSentAt());
            dto.setTotalRecipients(c.getTotalRecipients());
            dto.setSuccessfulDeliveries(c.getSuccessfulDeliveries());
            dto.setFailedDeliveries(c.getFailedDeliveries());
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailDeliveryLogDTO> getCampaignDeliveryLogs(UUID campaignId, Pageable pageable) {
        EmailCampaign campaign = emailCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new BusinessException("Campaign not found"));
        return emailDeliveryLogRepository.findByEmailCampaign(campaign, pageable).map(l -> {
            EmailDeliveryLogDTO dto = new EmailDeliveryLogDTO();
            dto.setId(l.getId());
            dto.setRecipientEmail(l.getRecipientEmail());
            dto.setStatus(l.getStatus());
            dto.setErrorMessage(l.getErrorMessage());
            dto.setSentAt(l.getSentAt());
            return dto;
        });
    }

    @Override
    @org.springframework.scheduling.annotation.Async("taskExecutor")
    public void processScheduledEmails() {
        // Find campaigns that are SENDING or (PENDING and past scheduled time)
        // Here we mock the sending process
        List<EmailCampaign> campaigns = emailCampaignRepository.findAll().stream()
                .filter(c -> "SENDING".equals(c.getStatus()) || 
                             ("PENDING".equals(c.getStatus()) && c.getScheduledAt() != null && c.getScheduledAt().isBefore(LocalDateTime.now())))
                .toList();

        for (EmailCampaign campaign : campaigns) {
            campaign.setStatus("SENDING");
            emailCampaignRepository.save(campaign);
            
            int success = 0;
            int failed = 0;
            
            // Get all pending logs for this campaign
            List<EmailDeliveryLog> logs = emailDeliveryLogRepository.findAll().stream()
                    .filter(l -> l.getEmailCampaign().getId().equals(campaign.getId()) && "PENDING".equals(l.getStatus()))
                    .toList();
            
            for (EmailDeliveryLog log : logs) {
                // Mock SMTP Call Here
                try {
                    // MOCK SUCCESS
                    log.setStatus("SENT");
                    log.setSentAt(LocalDateTime.now());
                    success++;
                } catch (Exception e) {
                    log.setStatus("FAILED");
                    log.setErrorMessage(e.getMessage());
                    failed++;
                }
                emailDeliveryLogRepository.save(log);
            }
            
            campaign.setSuccessfulDeliveries(campaign.getSuccessfulDeliveries() + success);
            campaign.setFailedDeliveries(campaign.getFailedDeliveries() + failed);
            
            // If all logs are processed
            long remaining = emailDeliveryLogRepository.findAll().stream()
                    .filter(l -> l.getEmailCampaign().getId().equals(campaign.getId()) && "PENDING".equals(l.getStatus()))
                    .count();
                    
            if (remaining == 0) {
                campaign.setStatus("COMPLETED");
                campaign.setSentAt(LocalDateTime.now());
            }
            emailCampaignRepository.save(campaign);
        }
    }
}
