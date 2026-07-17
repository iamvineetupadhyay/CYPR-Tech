package com.cypr.modules.communication.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class HomepageBannerResponseDTO {

    private UUID id;
    private String imageUrl;
    private String linkUrl;
    private boolean isActive;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
