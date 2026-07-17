package com.cypr.modules.system.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class FeatureFlagRequestDTO {

    @NotBlank
    private String flagKey;

    private boolean isEnabled;

    private String description;

    public String getFlagKey() { return flagKey; }
    public void setFlagKey(String flagKey) { this.flagKey = flagKey; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
