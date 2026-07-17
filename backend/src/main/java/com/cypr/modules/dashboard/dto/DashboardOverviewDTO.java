package com.cypr.modules.dashboard.dto;

import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import java.util.List;

public class DashboardOverviewDTO {
    private DashboardMetricsDTO metrics;
    private ServerStatisticsDTO serverStats;
    private List<ActivityLogResponseDTO> recentActivities;
    private List<UserOverviewDTO> recentRegistrations;

    public DashboardMetricsDTO getMetrics() {
        return metrics;
    }

    public void setMetrics(DashboardMetricsDTO metrics) {
        this.metrics = metrics;
    }

    public ServerStatisticsDTO getServerStats() {
        return serverStats;
    }

    public void setServerStats(ServerStatisticsDTO serverStats) {
        this.serverStats = serverStats;
    }

    public List<ActivityLogResponseDTO> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<ActivityLogResponseDTO> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<UserOverviewDTO> getRecentRegistrations() {
        return recentRegistrations;
    }

    public void setRecentRegistrations(List<UserOverviewDTO> recentRegistrations) {
        this.recentRegistrations = recentRegistrations;
    }
}
