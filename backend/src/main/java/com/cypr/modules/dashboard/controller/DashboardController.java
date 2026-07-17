package com.cypr.modules.dashboard.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.dashboard.dto.DashboardMetricsDTO;
import com.cypr.modules.dashboard.dto.DashboardOverviewDTO;
import com.cypr.modules.dashboard.dto.ServerStatisticsDTO;
import com.cypr.modules.dashboard.dto.UserOverviewDTO;
import com.cypr.modules.dashboard.service.DashboardService;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@Tag(name = "Dashboard API", description = "Endpoints for retrieving aggregated admin dashboard metrics")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get Unified Dashboard Payload", description = "Single endpoint returning aggregated { overview, metrics, server } data to minimize HTTP overhead")
    public ResponseEntity<BaseResponse<DashboardOverviewDTO>> getUnifiedDashboard() {
        return ResponseEntity.ok(BaseResponse.success("Dashboard data retrieved successfully", dashboardService.getOverview()));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get Dashboard Metrics", description = "Retrieves aggregated counts for revenue, users, credits, threats, etc.")
    public ResponseEntity<BaseResponse<DashboardMetricsDTO>> getMetrics() {
        return ResponseEntity.ok(BaseResponse.success("Metrics retrieved", dashboardService.getMetrics()));
    }

    @GetMapping("/server-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Server Statistics", description = "Retrieves current CPU, memory, and JVM uptime (Restricted to ADMIN)")
    public ResponseEntity<BaseResponse<ServerStatisticsDTO>> getServerStats() {
        return ResponseEntity.ok(BaseResponse.success("Server stats retrieved", dashboardService.getServerStats()));
    }

    @GetMapping("/recent-activities")
    @Operation(summary = "Get Recent Activities", description = "Retrieves a paginated list of recent user and system activities")
    public ResponseEntity<BaseResponse<Page<ActivityLogResponseDTO>>> getRecentActivities(Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Activities retrieved", dashboardService.getRecentActivities(pageable)));
    }

    @GetMapping("/recent-registrations")
    @Operation(summary = "Get Recent Registrations", description = "Retrieves a list of the 10 most recent user signups")
    public ResponseEntity<BaseResponse<List<UserOverviewDTO>>> getRecentRegistrations() {
        return ResponseEntity.ok(BaseResponse.success("Registrations retrieved", dashboardService.getRecentRegistrations()));
    }

    @GetMapping("/overview")
    @Operation(summary = "Get Complete Dashboard Overview", description = "Retrieves all metrics, stats, activities, and registrations in one payload")
    public ResponseEntity<BaseResponse<DashboardOverviewDTO>> getOverview() {
        return ResponseEntity.ok(BaseResponse.success("Overview retrieved", dashboardService.getOverview()));
    }
}
