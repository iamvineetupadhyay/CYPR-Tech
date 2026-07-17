package com.cypr.modules.security.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class FraudDetectionResponseDTO {
    private UUID id;
    private Long userId;
    private String pattern;
    private int riskScoreImpact;
    private String status;
    private String details;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getRiskScoreImpact() {
        return riskScoreImpact;
    }

    public void setRiskScoreImpact(int riskScoreImpact) {
        this.riskScoreImpact = riskScoreImpact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
