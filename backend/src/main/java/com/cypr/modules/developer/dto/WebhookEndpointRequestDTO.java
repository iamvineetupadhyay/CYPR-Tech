package com.cypr.modules.developer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WebhookEndpointRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Secret is required")
    private String secret;

    @NotBlank(message = "Events are required")
    private String events; // JSON array string

    private boolean isActive = true;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public String getEvents() { return events; }
    public void setEvents(String events) { this.events = events; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
