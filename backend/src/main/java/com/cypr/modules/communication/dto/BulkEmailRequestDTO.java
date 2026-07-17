package com.cypr.modules.communication.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BulkEmailRequestDTO {

    @NotBlank(message = "Subject is required")
    private String subject;

    private String bodyHtml;

    private UUID templateId; // Optional if using a predefined template

    private List<Long> recipientUserIds; // If empty, could send to all users based on a filter

    private String subscriptionTypeFilter; // Optional filter to target e.g. "PREMIUM"

    private LocalDateTime scheduledAt; // If null, send immediately

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public List<Long> getRecipientUserIds() {
        return recipientUserIds;
    }

    public void setRecipientUserIds(List<Long> recipientUserIds) {
        this.recipientUserIds = recipientUserIds;
    }

    public String getSubscriptionTypeFilter() {
        return subscriptionTypeFilter;
    }

    public void setSubscriptionTypeFilter(String subscriptionTypeFilter) {
        this.subscriptionTypeFilter = subscriptionTypeFilter;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
