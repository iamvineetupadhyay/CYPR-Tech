package com.cypr.modules.security.entity;

import com.cypr.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_notification_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category"})
})
public class UserNotificationPreference extends BaseEntity {

    public static final Set<String> MANDATORY_CATEGORIES = Set.of(
            "SCORE_CRITICAL",
            "BREACH_FOUND",
            "UNRECOGNIZED_LOGIN",
            "PASSWORD_CHANGED",
            "MFA_TOGGLED",
            "SESSION_REVOKED",
            "EMAIL_CHANGED",
            "ACCOUNT_RECOVERY"
    );

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean enabled = true;

    public UserNotificationPreference() {}

    public UserNotificationPreference(Long userId, String category, boolean enabled) {
        this.userId = userId;
        this.category = category;
        this.enabled = enabled;
        validateMandatoryProtection();
    }

    @PrePersist
    @PreUpdate
    public void validateMandatoryProtection() {
        if (category != null && MANDATORY_CATEGORIES.contains(category.toUpperCase()) && !enabled) {
            throw new IllegalArgumentException("Mandatory security category [" + category + "] cannot be disabled.");
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        validateMandatoryProtection();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        validateMandatoryProtection();
    }
}
