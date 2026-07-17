package com.cypr.modules.security.entity;

import com.cypr.common.entity.BaseEntity;
import com.cypr.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fraud_detections")
public class FraudDetection extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String pattern; // e.g., MULTIPLE_FAILED_LOGINS, UNUSUAL_IP, RAPID_CREDIT_CONSUMPTION

    @Column(nullable = false)
    private int riskScoreImpact;

    @Column(nullable = false)
    private String status; // DETECTED, INVESTIGATING, RESOLVED, FALSE_POSITIVE

    @Column(columnDefinition = "TEXT")
    private String details;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
