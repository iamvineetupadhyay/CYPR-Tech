package com.cypr.modules.developer.dto;

import java.util.UUID;

public class WebhookEventLogDTO {
    private UUID id;
    private UUID endpointId;
    private String eventType;
    private String payload;
    private Integer responseCode;
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEndpointId() { return endpointId; }
    public void setEndpointId(UUID endpointId) { this.endpointId = endpointId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Integer getResponseCode() { return responseCode; }
    public void setResponseCode(Integer responseCode) { this.responseCode = responseCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
