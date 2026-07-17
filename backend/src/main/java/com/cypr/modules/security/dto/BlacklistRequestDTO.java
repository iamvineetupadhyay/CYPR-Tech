package com.cypr.modules.security.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class BlacklistRequestDTO {

    @NotBlank(message = "IP Address is required")
    private String ipAddress;

    @NotBlank(message = "Reason is required")
    private String reason;

    private Integer durationHours; // Optional

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }
}
