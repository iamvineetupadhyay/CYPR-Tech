package com.cypr.modules.communication.entity;

import com.cypr.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_campaigns")
public class EmailCampaign extends BaseEntity {

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(nullable = false)
    private String status; // PENDING, SENDING, COMPLETED, FAILED

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;

    private int totalRecipients = 0;
    private int successfulDeliveries = 0;
    private int failedDeliveries = 0;

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
