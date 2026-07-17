package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.entity.UserNotificationPreference;
import com.cypr.modules.security.repository.UserNotificationPreferenceRepository;
import com.cypr.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/notifications/preferences")
@Tag(name = "Notification Preferences API", description = "Endpoints for managing critical event email subscription preferences")
public class NotificationPreferenceController {

    private final UserNotificationPreferenceRepository preferenceRepository;

    public NotificationPreferenceController(UserNotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @GetMapping
    @Operation(summary = "Get User Notification Preferences")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getPreferences(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUserId(request);
        if (userId == null) userId = 1L;

        List<UserNotificationPreference> userPrefs = preferenceRepository.findByUserId(userId);
        Map<String, Boolean> states = new HashMap<>();

        // Default toggleable options
        states.put("WEEKLY_SUMMARY", true);
        states.put("MARKETING_UPDATES", false);

        // Mandatory alert indicators (Always true, cannot be disabled)
        states.put("SCORE_CRITICAL", true);
        states.put("BREACH_FOUND", true);
        states.put("UNRECOGNIZED_LOGIN", true);
        states.put("PASSWORD_CHANGED", true);
        states.put("MFA_TOGGLED", true);

        for (UserNotificationPreference pref : userPrefs) {
            states.put(pref.getCategory(), pref.isEnabled());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("preferences", states);
        response.put("mandatoryCategories", UserNotificationPreference.MANDATORY_CATEGORIES);

        return ResponseEntity.ok(BaseResponse.success("Preferences retrieved", response));
    }

    @PutMapping
    @Operation(summary = "Update Notification Preferences")
    public ResponseEntity<BaseResponse<Void>> updatePreferences(
            @RequestBody Map<String, Boolean> preferences,
            HttpServletRequest request) {

        Long rawUserId = SecurityUtils.getCurrentUserId(request);
        final Long effectiveUserId = rawUserId != null ? rawUserId : 1L;

        for (Map.Entry<String, Boolean> entry : preferences.entrySet()) {
            String category = entry.getKey().toUpperCase();
            boolean enabled = Boolean.TRUE.equals(entry.getValue());

            if (UserNotificationPreference.MANDATORY_CATEGORIES.contains(category) && !enabled) {
                return ResponseEntity.badRequest().body(BaseResponse.error("Category [" + category + "] is mandatory for security integrity and cannot be disabled."));
            }

            UserNotificationPreference pref = preferenceRepository.findByUserIdAndCategory(effectiveUserId, category)
                    .orElseGet(() -> new UserNotificationPreference(effectiveUserId, category, true));

            pref.setEnabled(enabled);
            preferenceRepository.save(pref);
        }

        return ResponseEntity.ok(BaseResponse.success("Notification preferences updated successfully", null));
    }
}
