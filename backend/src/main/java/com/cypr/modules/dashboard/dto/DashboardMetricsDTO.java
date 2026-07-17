package com.cypr.modules.dashboard.dto;

import java.math.BigDecimal;

public class DashboardMetricsDTO {
    private BigDecimal totalRevenue;
    private long totalUsers;
    private long activeCredits;
    private long securityThreats;
    private long failedLogins;
    private long onlineUsers;
    private long premiumUsers;
    private long totalScans;
    private long auditEvents;

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveCredits() {
        return activeCredits;
    }

    public void setActiveCredits(long activeCredits) {
        this.activeCredits = activeCredits;
    }

    public long getSecurityThreats() {
        return securityThreats;
    }

    public void setSecurityThreats(long securityThreats) {
        this.securityThreats = securityThreats;
    }

    public long getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(long failedLogins) {
        this.failedLogins = failedLogins;
    }

    public long getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(long onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public long getPremiumUsers() {
        return premiumUsers;
    }

    public void setPremiumUsers(long premiumUsers) {
        this.premiumUsers = premiumUsers;
    }

    public long getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(long totalScans) {
        this.totalScans = totalScans;
    }

    public long getAuditEvents() {
        return auditEvents;
    }

    public void setAuditEvents(long auditEvents) {
        this.auditEvents = auditEvents;
    }
}
