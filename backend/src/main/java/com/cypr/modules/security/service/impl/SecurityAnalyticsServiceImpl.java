package com.cypr.modules.security.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.*;
import com.cypr.modules.security.entity.ActivityLog;
import com.cypr.modules.security.entity.BlacklistedIp;
import com.cypr.modules.security.entity.FraudDetection;
import com.cypr.modules.security.entity.SecurityLog;
import com.cypr.modules.security.repository.ActivityLogRepository;
import com.cypr.modules.security.repository.BlacklistedIpRepository;
import com.cypr.modules.security.repository.FraudDetectionRepository;
import com.cypr.modules.security.repository.SecurityLogRepository;
import com.cypr.modules.security.service.SecurityAnalyticsService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SecurityAnalyticsServiceImpl implements SecurityAnalyticsService {

    private final SecurityLogRepository securityLogRepository;
    private final ActivityLogRepository activityLogRepository;
    private final BlacklistedIpRepository blacklistedIpRepository;
    private final FraudDetectionRepository fraudDetectionRepository;
    private final UserRepository userRepository;

    public SecurityAnalyticsServiceImpl(
            SecurityLogRepository securityLogRepository,
            ActivityLogRepository activityLogRepository,
            BlacklistedIpRepository blacklistedIpRepository,
            FraudDetectionRepository fraudDetectionRepository,
            UserRepository userRepository) {
        this.securityLogRepository = securityLogRepository;
        this.activityLogRepository = activityLogRepository;
        this.blacklistedIpRepository = blacklistedIpRepository;
        this.fraudDetectionRepository = fraudDetectionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SecurityTimelineDTO> getUnifiedTimeline(SecuritySearchCriteriaDTO criteria, Pageable pageable) {
        // Fetch matching SecurityLogs
        Specification<SecurityLog> secSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                if (criteria.getIpAddress() != null && !criteria.getIpAddress().isEmpty())
                    predicates.add(cb.equal(root.get("ipAddress"), criteria.getIpAddress()));
                if (criteria.getCountry() != null && !criteria.getCountry().isEmpty())
                    predicates.add(cb.equal(root.get("country"), criteria.getCountry()));
                if (criteria.getSeverity() != null && !criteria.getSeverity().isEmpty())
                    predicates.add(cb.equal(root.get("severity"), criteria.getSeverity()));
                if (criteria.getFromDate() != null)
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getFromDate()));
                if (criteria.getToDate() != null)
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getToDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Fetch matching ActivityLogs
        Specification<ActivityLog> actSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                if (criteria.getIpAddress() != null && !criteria.getIpAddress().isEmpty())
                    predicates.add(cb.equal(root.get("ipAddress"), criteria.getIpAddress()));
                if (criteria.getCountry() != null && !criteria.getCountry().isEmpty())
                    predicates.add(cb.equal(root.get("country"), criteria.getCountry()));
                if (criteria.getFromDate() != null)
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getFromDate()));
                if (criteria.getToDate() != null)
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getToDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Since doing a true unified pagination across two separate tables in SQL is complex (UNION ALL),
        // we'll fetch them, merge in memory, and sort. This is a simplified approach for the admin dashboard.
        
        List<SecurityLog> secLogs = securityLogRepository.findAll(secSpec, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ActivityLog> actLogs = activityLogRepository.findAll(actSpec, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<SecurityTimelineDTO> timeline = new ArrayList<>();

        if (criteria == null || criteria.getType() == null || criteria.getType().equalsIgnoreCase("SECURITY")) {
            for (SecurityLog log : secLogs) {
                SecurityTimelineDTO dto = new SecurityTimelineDTO();
                dto.setId(log.getId());
                if (log.getUser() != null) {
                    dto.setUserId(log.getUser().getId());
                    dto.setUsername(log.getUser().getUsername());
                }
                dto.setType("SECURITY");
                dto.setEvent(log.getEvent());
                dto.setDetails(log.getAction() != null ? log.getAction() : "N/A");
                dto.setIpAddress(log.getIpAddress());
                dto.setUserAgent(log.getUserAgent());
                dto.setCountry(log.getCountry());
                dto.setSeverity(log.getSeverity());
                dto.setTimestamp(log.getCreatedAt());
                timeline.add(dto);
            }
        }

        if (criteria == null || criteria.getType() == null || criteria.getType().equalsIgnoreCase("ACTIVITY")) {
            for (ActivityLog log : actLogs) {
                SecurityTimelineDTO dto = new SecurityTimelineDTO();
                dto.setId(log.getId());
                if (log.getUser() != null) {
                    dto.setUserId(log.getUser().getId());
                    dto.setUsername(log.getUser().getUsername());
                }
                dto.setType("ACTIVITY");
                dto.setEvent(log.getAction());
                dto.setDetails(log.getDetails());
                dto.setIpAddress(log.getIpAddress());
                dto.setUserAgent(log.getUserAgent());
                dto.setCountry(log.getCountry());
                dto.setSeverity("INFO");
                dto.setTimestamp(log.getCreatedAt());
                timeline.add(dto);
            }
        }

        // Sort descending
        timeline.sort(Comparator.comparing(SecurityTimelineDTO::getTimestamp).reversed());

        // Paginate in memory
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), timeline.size());
        List<SecurityTimelineDTO> subList = start > timeline.size() ? new ArrayList<>() : timeline.subList(start, end);

        return new PageImpl<>(subList, pageable, timeline.size());
    }

    @Override
    @Transactional(readOnly = true)
    public RiskScoreDTO calculateUserRiskScore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        int score = 0;

        // 1. Failed Logins Impact
        Page<SecurityLog> recentLogs = securityLogRepository.findByUser(user, Pageable.ofSize(50));
        long recentFailedLogins = recentLogs.stream()
                .filter(l -> "FAILED_LOGIN".equals(l.getEvent()))
                .count();
        score += recentFailedLogins * 10;

        // 2. High Severity events
        long highSeverity = recentLogs.stream()
                .filter(l -> "HIGH".equals(l.getSeverity()) || "CRITICAL".equals(l.getSeverity()))
                .count();
        score += highSeverity * 20;

        // 3. Fraud Patterns
        List<FraudDetection> fraudList = fraudDetectionRepository.findByUser(user);
        for (FraudDetection f : fraudList) {
            if ("DETECTED".equals(f.getStatus()) || "INVESTIGATING".equals(f.getStatus())) {
                score += f.getRiskScoreImpact();
            }
        }

        // Cap at 100
        score = Math.min(score, 100);

        RiskScoreDTO dto = new RiskScoreDTO();
        dto.setUserId(userId);
        dto.setScore(score);

        if (score < 20) {
            dto.setRiskLevel("LOW");
            dto.setRecommendation("No action needed.");
        } else if (score < 50) {
            dto.setRiskLevel("MEDIUM");
            dto.setRecommendation("Monitor user activity. Consider prompting 2FA.");
        } else if (score < 80) {
            dto.setRiskLevel("HIGH");
            dto.setRecommendation("Investigate immediately. User might be compromised.");
        } else {
            dto.setRiskLevel("CRITICAL");
            dto.setRecommendation("Lock account immediately and force password reset.");
        }

        return dto;
    }

    @Override
    public BlacklistedIp blacklistIp(BlacklistRequestDTO requestDTO) {
        if (blacklistedIpRepository.existsByIpAddress(requestDTO.getIpAddress())) {
            throw new BusinessException("IP is already blacklisted");
        }
        
        BlacklistedIp ip = new BlacklistedIp();
        ip.setIpAddress(requestDTO.getIpAddress());
        ip.setReason(requestDTO.getReason());
        
        if (requestDTO.getDurationHours() != null) {
            ip.setExpiresAt(LocalDateTime.now().plusHours(requestDTO.getDurationHours()));
        }

        return blacklistedIpRepository.save(ip);
    }

    @Override
    public void removeIpFromBlacklist(String ipAddress) {
        BlacklistedIp ip = blacklistedIpRepository.findByIpAddress(ipAddress)
                .orElseThrow(() -> new BusinessException("IP not found in blacklist"));
        blacklistedIpRepository.delete(ip);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FraudDetectionResponseDTO> getFraudDetections(Pageable pageable) {
        return fraudDetectionRepository.findAll(pageable).map(f -> {
            FraudDetectionResponseDTO dto = new FraudDetectionResponseDTO();
            dto.setId(f.getId());
            if (f.getUser() != null) dto.setUserId(f.getUser().getId());
            dto.setPattern(f.getPattern());
            dto.setRiskScoreImpact(f.getRiskScoreImpact());
            dto.setStatus(f.getStatus());
            dto.setDetails(f.getDetails());
            dto.setCreatedAt(f.getCreatedAt());
            return dto;
        });
    }

    @Override
    public void scanForThreats() {
        // Scheduled task placeholder. 
        // Example: Finds users with >5 failed logins in last hour, creates a FraudDetection record.
    }
}
