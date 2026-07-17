package com.cypr.modules.security.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class DeviceResponseDTO {

    private UUID id;
    private Long userId;
    private String fingerprint;
    private String userAgent;
    private String os;
    private String browser;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
}
