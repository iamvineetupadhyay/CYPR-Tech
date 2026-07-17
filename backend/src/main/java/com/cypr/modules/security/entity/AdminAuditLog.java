package com.cypr.modules.security.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;
    private String adminEmail;
    private String action;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;
    private String correlationId;
    private LocalDateTime createdAt = LocalDateTime.now();

    public AdminAuditLog() {}

    public AdminAuditLog(Long adminId, String adminEmail, String action, String details, String ipAddress, String correlationId) {
        this.adminId = adminId;
        this.adminEmail = adminEmail;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.correlationId = correlationId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
