package com.cypr.modules.support.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class TicketReplyResponseDTO {

    private UUID id;
    private UUID ticketId;
    private Long userId;
    private String message;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTicketId() { return ticketId; }
    public void setTicketId(UUID ticketId) { this.ticketId = ticketId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
