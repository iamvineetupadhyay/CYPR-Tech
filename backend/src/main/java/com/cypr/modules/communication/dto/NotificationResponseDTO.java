package com.cypr.modules.communication.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class NotificationResponseDTO {

    private UUID id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
}
