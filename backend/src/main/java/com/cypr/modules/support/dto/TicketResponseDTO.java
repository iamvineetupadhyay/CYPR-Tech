package com.cypr.modules.support.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class TicketResponseDTO {

    private UUID id;
    private Long userId;
    private String subject;
    private String description;
    private String status;
    private String priority;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
