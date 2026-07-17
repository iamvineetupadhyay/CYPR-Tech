package com.cypr.modules.developer.dto;

import java.util.UUID;

public class WebhookEndpointResponseDTO {
    private UUID id;
    private Long userId;
    private String url;
    private String events;
    private boolean isActive;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getEvents() { return events; }
    public void setEvents(String events) { this.events = events; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
