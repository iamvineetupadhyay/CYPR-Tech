package com.cypr.modules.security.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class SecurityLogRequestDTO {

    private Long userId;

    @NotBlank
    private String ipAddress;

    @NotBlank
    private String severity;

    @NotBlank
    private String event;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
}
