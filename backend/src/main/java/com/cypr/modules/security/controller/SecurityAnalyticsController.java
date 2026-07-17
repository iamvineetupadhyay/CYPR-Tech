package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.*;
import com.cypr.modules.security.entity.BlacklistedIp;
import com.cypr.modules.security.service.SecurityAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/security")
@Tag(name = "Security Analytics", description = "APIs for Threat Detection, Fraud, Blacklists, and Unified Timelines")
public class SecurityAnalyticsController {

    private final SecurityAnalyticsService securityAnalyticsService;

    public SecurityAnalyticsController(SecurityAnalyticsService securityAnalyticsService) {
        this.securityAnalyticsService = securityAnalyticsService;
    }

    @GetMapping("/timeline")
    @Operation(summary = "Get Unified Security Timeline")
    public ResponseEntity<BaseResponse<Page<SecurityTimelineDTO>>> getUnifiedTimeline(
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        SecuritySearchCriteriaDTO criteria = new SecuritySearchCriteriaDTO();
        criteria.setIpAddress(ipAddress);
        criteria.setCountry(country);
        criteria.setSeverity(severity);
        criteria.setType(type);

        Pageable pageable = PageRequest.of(page, size);
        Page<SecurityTimelineDTO> timeline = securityAnalyticsService.getUnifiedTimeline(criteria, pageable);

        return ResponseEntity.ok(BaseResponse.success("Timeline fetched successfully", timeline));
    }

    @GetMapping("/risk-score/{userId}")
    @Operation(summary = "Calculate Real-Time Risk Score for User")
    public ResponseEntity<BaseResponse<RiskScoreDTO>> getRiskScore(@PathVariable Long userId) {
        RiskScoreDTO score = securityAnalyticsService.calculateUserRiskScore(userId);
        return ResponseEntity.ok(BaseResponse.success("Risk score calculated", score));
    }

    @PostMapping("/blacklist")
    @Operation(summary = "Blacklist an IP Address")
    public ResponseEntity<BaseResponse<BlacklistedIp>> blacklistIp(@Valid @RequestBody BlacklistRequestDTO requestDTO) {
        BlacklistedIp blacklistedIp = securityAnalyticsService.blacklistIp(requestDTO);
        return ResponseEntity.ok(BaseResponse.success("IP blacklisted successfully", blacklistedIp));
    }

    @DeleteMapping("/blacklist/{ipAddress}")
    @Operation(summary = "Remove IP Address from Blacklist")
    public ResponseEntity<BaseResponse<Void>> removeBlacklist(@PathVariable String ipAddress) {
        securityAnalyticsService.removeIpFromBlacklist(ipAddress);
        return ResponseEntity.ok(BaseResponse.success("IP removed from blacklist", null));
    }

    @GetMapping("/fraud")
    @Operation(summary = "Get Fraud Detection Logs")
    public ResponseEntity<BaseResponse<Page<FraudDetectionResponseDTO>>> getFraudDetections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<FraudDetectionResponseDTO> frauds = securityAnalyticsService.getFraudDetections(pageable);
        return ResponseEntity.ok(BaseResponse.success("Fraud logs fetched successfully", frauds));
    }
}
