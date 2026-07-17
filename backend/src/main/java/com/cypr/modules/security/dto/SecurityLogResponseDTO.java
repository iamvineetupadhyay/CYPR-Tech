package com.cypr.modules.security.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class SecurityLogResponseDTO {

    private UUID id;
    private Long userId;
    private String ipAddress;
    private String severity;
    private String event;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
}
