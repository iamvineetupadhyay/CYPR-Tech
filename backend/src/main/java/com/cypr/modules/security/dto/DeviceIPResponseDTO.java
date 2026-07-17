package com.cypr.modules.security.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class DeviceIPResponseDTO {

    private UUID id;
    private UUID deviceId;
    private String ipAddress;
    private LocalDateTime lastSeenAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
}
