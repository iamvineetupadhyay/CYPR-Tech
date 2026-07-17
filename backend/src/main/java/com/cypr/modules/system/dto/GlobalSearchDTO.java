package com.cypr.modules.system.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class GlobalSearchDTO {
    private String id; // Use String to support both Long (User) and UUID
    private String type; // USER, TICKET, PAYMENT, LOG, SETTING, ANNOUNCEMENT
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private String url;

    public GlobalSearchDTO() {}

    public GlobalSearchDTO(String id, String type, String title, String description, LocalDateTime timestamp, String url) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.url = url;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
