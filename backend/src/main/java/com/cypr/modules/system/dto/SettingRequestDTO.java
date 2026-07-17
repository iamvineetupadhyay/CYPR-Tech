package com.cypr.modules.system.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class SettingRequestDTO {

    @NotBlank
    private String settingKey;

    @NotBlank
    private String settingValue;

    private String group;

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
