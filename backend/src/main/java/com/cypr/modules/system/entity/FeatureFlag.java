package com.cypr.modules.system.entity;

import com.cypr.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.Version;

@Entity
@Table(name = "feature_flags")
public class FeatureFlag extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String flagKey;

    @Column(nullable = false)
    private boolean isEnabled = false;

    private String description;

    @Version
    private Long version;

    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(String flagKey) {
        this.flagKey = flagKey;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
