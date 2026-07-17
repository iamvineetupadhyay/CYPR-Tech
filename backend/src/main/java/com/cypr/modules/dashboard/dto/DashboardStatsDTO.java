package com.cypr.modules.dashboard.dto;

public class DashboardStatsDTO {
    private Long totalUsers;
    private Long activeThreats;
    private Long totalScans;

    public DashboardStatsDTO() {}

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getActiveThreats() {
        return activeThreats;
    }

    public void setActiveThreats(Long activeThreats) {
        this.activeThreats = activeThreats;
    }

    public Long getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(Long totalScans) {
        this.totalScans = totalScans;
    }
}
