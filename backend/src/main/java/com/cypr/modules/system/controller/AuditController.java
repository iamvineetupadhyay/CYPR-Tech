package com.cypr.modules.system.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.service.ActivityLogService;
import com.cypr.modules.security.service.SecurityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit API", description = "Endpoints for auditing and compliance logs")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SECURITY', 'AUDITOR')")
public class AuditController {

    private final ActivityLogService activityLogService;
    private final SecurityLogService securityLogService;

    public AuditController(ActivityLogService activityLogService, SecurityLogService securityLogService) {
        this.activityLogService = activityLogService;
        this.securityLogService = securityLogService;
    }

    @GetMapping("/activities")
    @Operation(summary = "Get Activity Logs", description = "Retrieve a paginated list of all system activity logs")
    public ResponseEntity<BaseResponse<Page<ActivityLogResponseDTO>>> getActivityLogs(Pageable pageable) {
        Page<ActivityLogResponseDTO> page = activityLogService.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved activity logs successfully", page));
    }

    @GetMapping("/security")
    @Operation(summary = "Get Security Logs", description = "Retrieve a paginated list of all system security logs (threats, blocked IPs)")
    public ResponseEntity<BaseResponse<Page<SecurityLogResponseDTO>>> getSecurityLogs(Pageable pageable) {
        Page<SecurityLogResponseDTO> page = securityLogService.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved security logs successfully", page));
    }
}
