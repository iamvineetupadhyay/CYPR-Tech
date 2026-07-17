package com.cypr.modules.dashboard.mapper;

import com.cypr.modules.dashboard.dto.DashboardStatsDTO;
import com.cypr.modules.dashboard.entity.DashboardMetric;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardMapper {
    
    // Example mapping logic
    public DashboardStatsDTO toDto(List<DashboardMetric> metrics) {
        return new DashboardStatsDTO();
    }
}
