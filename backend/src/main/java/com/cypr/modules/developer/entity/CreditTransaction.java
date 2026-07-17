package com.cypr.modules.developer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_transactions")
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetUserId;
    private Long adminId;
    private Integer amount;
    private String operation; // GRANT / REVOKE / ADJUST
    private String reason;
    private String ipAddress;
    private String correlationId;
    private LocalDateTime createdAt = LocalDateTime.now();

    public CreditTransaction() {}

    public CreditTransaction(Long targetUserId, Long adminId, Integer amount, String operation, String reason, String ipAddress, String correlationId) {
        this.targetUserId = targetUserId;
        this.adminId = adminId;
        this.amount = amount;
        this.operation = operation;
        this.reason = reason;
        this.ipAddress = ipAddress;
        this.correlationId = correlationId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
