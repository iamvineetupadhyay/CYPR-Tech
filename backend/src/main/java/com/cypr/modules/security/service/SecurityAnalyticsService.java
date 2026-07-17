package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.BlacklistRequestDTO;
import com.cypr.modules.security.dto.FraudDetectionResponseDTO;
import com.cypr.modules.security.dto.RiskScoreDTO;
import com.cypr.modules.security.dto.SecuritySearchCriteriaDTO;
import com.cypr.modules.security.dto.SecurityTimelineDTO;
import com.cypr.modules.security.entity.BlacklistedIp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SecurityAnalyticsService {
    Page<SecurityTimelineDTO> getUnifiedTimeline(SecuritySearchCriteriaDTO criteria, Pageable pageable);
    RiskScoreDTO calculateUserRiskScore(Long userId);
    BlacklistedIp blacklistIp(BlacklistRequestDTO requestDTO);
    void removeIpFromBlacklist(String ipAddress);
    Page<FraudDetectionResponseDTO> getFraudDetections(Pageable pageable);
    void scanForThreats(); // Programmatic anomaly detection
}
