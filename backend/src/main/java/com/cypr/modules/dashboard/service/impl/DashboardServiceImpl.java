package com.cypr.modules.dashboard.service.impl;

import com.cypr.modules.dashboard.dto.DashboardMetricsDTO;
import com.cypr.modules.dashboard.dto.DashboardOverviewDTO;
import com.cypr.modules.dashboard.dto.ServerStatisticsDTO;
import com.cypr.modules.dashboard.dto.UserOverviewDTO;
import com.cypr.modules.dashboard.service.DashboardService;
import com.cypr.modules.billing.repository.PaymentRepository;
import com.cypr.modules.developer.repository.CreditRepository;
import com.cypr.modules.security.repository.SecurityLogRepository;
import com.cypr.modules.security.repository.SessionRepository;
import com.cypr.modules.security.repository.ActivityLogRepository;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import com.cypr.modules.security.mapper.ActivityLogMapper;
import com.cypr.repository.UserRepository;
import com.cypr.repository.ScanRepository;
import com.cypr.repository.MalwareScanLogRepository;
import com.cypr.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CreditRepository creditRepository;
    private final SecurityLogRepository securityLogRepository;
    private final SessionRepository sessionRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final ScanRepository scanRepository;
    private final MalwareScanLogRepository malwareScanLogRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            CreditRepository creditRepository,
            SecurityLogRepository securityLogRepository,
            SessionRepository sessionRepository,
            ActivityLogRepository activityLogRepository,
            ActivityLogMapper activityLogMapper,
            ScanRepository scanRepository,
            MalwareScanLogRepository malwareScanLogRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.creditRepository = creditRepository;
        this.securityLogRepository = securityLogRepository;
        this.sessionRepository = sessionRepository;
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
        this.scanRepository = scanRepository;
        this.malwareScanLogRepository = malwareScanLogRepository;
    }

    @Override
    public DashboardMetricsDTO getMetrics() {
        DashboardMetricsDTO metrics = new DashboardMetricsDTO();
        
        metrics.setTotalRevenue(paymentRepository.getTotalRevenue());
        metrics.setTotalUsers(userRepository.count());
        
        Long creditBalance = userRepository.getTotalCreditBalance();
        metrics.setActiveCredits(creditBalance != null ? creditBalance : 0L);
        metrics.setPremiumUsers(userRepository.countBySubscriptionTypeIgnoreCase("PRO"));
        metrics.setTotalScans(scanRepository.count() + malwareScanLogRepository.count());
        metrics.setAuditEvents(activityLogRepository.count());
        
        metrics.setSecurityThreats(securityLogRepository.countHighSeverityThreats());
        metrics.setFailedLogins(securityLogRepository.countFailedLogins());
        
        // Count active sessions as online users
        metrics.setOnlineUsers(sessionRepository.count());

        return metrics;
    }

    @Override
    public ServerStatisticsDTO getServerStats() {
        ServerStatisticsDTO stats = new ServerStatisticsDTO();
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        stats.setCpuUsage(osBean.getSystemLoadAverage());

        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        long usedMem = totalMem - freeMem;
        
        stats.setMemoryUsed(usedMem);
        stats.setMemoryTotal(totalMem);

        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = uptimeMillis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        stats.setUptime(hours + "h " + minutes + "m");

        return stats;
    }

    @Override
    public Page<ActivityLogResponseDTO> getRecentActivities(Pageable pageable) {
        return activityLogRepository.findAll(pageable).map(activityLogMapper::toResponseDTO);
    }

    @Override
    public List<UserOverviewDTO> getRecentRegistrations() {
        Page<User> users = userRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        return users.stream().map(u -> {
            UserOverviewDTO dto = new UserOverviewDTO();
            dto.setId(u.getId());
            dto.setEmail(u.getEmail());
            dto.setCreatedAt(u.getCreatedAt());
            dto.setActive(u.isEnabled());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public DashboardOverviewDTO getOverview() {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        overview.setMetrics(getMetrics());
        overview.setServerStats(getServerStats());
        overview.setRecentActivities(getRecentActivities(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
        overview.setRecentRegistrations(getRecentRegistrations());
        return overview;
    }
}
