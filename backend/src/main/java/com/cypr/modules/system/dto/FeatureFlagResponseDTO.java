package com.cypr.modules.system.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class FeatureFlagResponseDTO {

    private UUID id;
    private String flagKey;
    private boolean isEnabled;
    private String description;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFlagKey() { return flagKey; }
    public void setFlagKey(String flagKey) { this.flagKey = flagKey; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
