package com.cypr.modules.dashboard.service;

import com.cypr.modules.dashboard.dto.DashboardMetricsDTO;
import com.cypr.modules.dashboard.dto.DashboardOverviewDTO;
import com.cypr.modules.dashboard.dto.ServerStatisticsDTO;
import com.cypr.modules.dashboard.dto.UserOverviewDTO;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DashboardService {
    DashboardMetricsDTO getMetrics();
    ServerStatisticsDTO getServerStats();
    Page<ActivityLogResponseDTO> getRecentActivities(Pageable pageable);
    List<UserOverviewDTO> getRecentRegistrations();
    DashboardOverviewDTO getOverview();
}
