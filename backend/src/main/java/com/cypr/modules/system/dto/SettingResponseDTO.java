package com.cypr.modules.system.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class SettingResponseDTO {

    private UUID id;
    private String settingKey;
    private String settingValue;
    private String group;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
