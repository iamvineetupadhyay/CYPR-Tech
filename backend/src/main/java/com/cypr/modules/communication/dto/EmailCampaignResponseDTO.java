package com.cypr.modules.communication.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailCampaignResponseDTO {
    private UUID id;
    private String subject;
    private String status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private int totalRecipients;
    private int successfulDeliveries;
    private int failedDeliveries;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
    }

    public int getSuccessfulDeliveries() {
        return successfulDeliveries;
    }

    public void setSuccessfulDeliveries(int successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }

    public int getFailedDeliveries() {
        return failedDeliveries;
    }

    public void setFailedDeliveries(int failedDeliveries) {
        this.failedDeliveries = failedDeliveries;
    }
}
